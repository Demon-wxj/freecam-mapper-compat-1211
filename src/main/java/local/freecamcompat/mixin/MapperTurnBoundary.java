package local.freecamcompat.mixin;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(value=Entity.class,priority=300)
public abstract class MapperTurnBoundary {}
