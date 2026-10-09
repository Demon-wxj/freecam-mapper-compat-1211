package local.freecamcompat.mixin;
import local.freecamcompat.CameraOwnership;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(value=Entity.class,priority=1200)
public abstract class CameraPermissionMixin {
    @ModifyVariable(method="changeLookDirection",at=@At("HEAD"),argsOnly=true,ordinal=0)
    private double horizontal(double delta){return CameraOwnership.filterAxis(this,delta,true);}
    @ModifyVariable(method="changeLookDirection",at=@At("HEAD"),argsOnly=true,ordinal=1)
    private double vertical(double delta){return CameraOwnership.filterAxis(this,delta,false);}
}
