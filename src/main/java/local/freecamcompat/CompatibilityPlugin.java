package local.freecamcompat;
import java.util.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.*;

/** Guard only Mapper's merged turn callback, preserving all its other hooks. */
public final class CompatibilityPlugin implements IMixinConfigPlugin {
    public void onLoad(String p){} public String getRefMapperConfig(){return null;}
    public boolean shouldApplyMixin(String t,String m){return true;}
    public void acceptTargets(Set<String>a,Set<String>b){} public List<String>getMixins(){return null;}
    public void preApply(String t,ClassNode n,String m,IMixinInfo i){}
    public void postApply(String t,ClassNode node,String mixin,IMixinInfo info){
        if(!mixin.endsWith(".MapperTurnBoundary"))return;
        int matches=0;
        for(var method:node.methods){
            if(!method.name.endsWith("$changeLookDirection")||!method.desc.equals("(DDLorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V")||method.visibleAnnotations==null)continue;
            boolean mapper=false;
            for(var annotation:method.visibleAnnotations)if(annotation.desc.equals("Lorg/spongepowered/asm/mixin/transformer/meta/MixinMerged;"))
                for(int j=0;j<annotation.values.size()-1;j+=2)if(annotation.values.get(j).equals("mixin")&&annotation.values.get(j+1).equals("steak.mapperplugin.mixin.EntityMixin"))mapper=true;
            if(!mapper)continue;
            var guard=new InsnList();var original=new LabelNode();
            guard.add(new VarInsnNode(Opcodes.ALOAD,0));
            guard.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"local/freecamcompat/CameraOwnership","yieldMapperTurn","(Ljava/lang/Object;)Z",false));
            guard.add(new JumpInsnNode(Opcodes.IFEQ,original));guard.add(new InsnNode(Opcodes.RETURN));guard.add(original);
            method.instructions.insert(guard);matches++;
        }
        if(matches!=1)throw new IllegalStateException("Expected exactly one MapperPlugin rotation callback; found "+matches);
        System.out.println("FREECAM_COMPAT MapperPlugin rotation ownership fixed");
    }
}
