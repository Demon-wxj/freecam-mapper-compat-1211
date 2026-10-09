package local.freecamcompat;

import java.lang.reflect.*;
import net.minecraft.client.MinecraftClient;

/** Bridge only to the pinned public APIs; no third-party bytecode is bundled. */
public final class CameraOwnership {
    private static final Method enabled,playerControl,camera,permission;
    private static final Field cinematic,yawAllowed,pitchAllowed;
    private static final Object horizontal,vertical;
    static {
        try {
            var api=Class.forName("net.xolt.freecam.Freecam");
            enabled=api.getMethod("isEnabled");playerControl=api.getMethod("isPlayerControlEnabled");camera=api.getMethod("getFreeCamera");
            var mapper=Class.forName("steak.mapperplugin.Utils.CameraUtil");
            cinematic=mapper.getField("cameraSetUse");yawAllowed=mapper.getField("playerAllowYawState");pitchAllowed=mapper.getField("playerAllowPitchState");
            var keys=Class.forName("steak.mapperplugin.ArgumentType.EnumType.InputPermissionArgumentType$Enum");
            horizontal=keys.getField("ROTATION_HORIZONTAL").get(null);vertical=keys.getField("ROTATION_VERTICAL").get(null);
            permission=Class.forName("steak.mapperplugin.Utils.InputManager$Permissions").getMethod("getPermission",keys);
        }catch(ReflectiveOperationException e){throw new IllegalStateException("Unsupported Freecam/MapperPlugin API",e);}
    }
    private static Object controlledCamera() throws ReflectiveOperationException {
        if(!(boolean)enabled.invoke(null)||(boolean)playerControl.invoke(null)||cinematic.getBoolean(null))return null;
        return camera.invoke(null);
    }
    public static boolean yieldMapperTurn(Object receiver) {
        try {
            Object view=controlledCamera();
            return view!=null&&(receiver==view||receiver==MinecraftClient.getInstance().player);
        }catch(ReflectiveOperationException e){throw new IllegalStateException("Cannot resolve camera ownership",e);}
    }
    public static double filterAxis(Object receiver,double delta,boolean yaw) {
        try {
            if(receiver!=controlledCamera())return delta;
            return (boolean)permission.invoke(null,yaw?horizontal:vertical)&&(yaw?yawAllowed:pitchAllowed).getBoolean(null)?delta:0;
        }catch(ReflectiveOperationException e){throw new IllegalStateException("Cannot read map rotation permission",e);}
    }
}
