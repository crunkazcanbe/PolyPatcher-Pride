package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC-2164: pearls put the thrower inside the block they hit. Move the landing point out of the face that was hit. */
@Mixin(EntityEnderPearl.class)
public abstract class EntityEnderPearlMixin_NoWalls extends EntityThrowable {
    public EntityEnderPearlMixin_NoWalls(World w) { super(w); }

    @Inject(method = "onImpact", at = @At("HEAD"))
    private void pride$backOff(RayTraceResult result, CallbackInfo ci) {
        if (!PatcherConfig.prideEnderPearlWalls || result.typeOfHit != RayTraceResult.Type.BLOCK || result.sideHit == null || result.hitVec == null) return;
        EntityLivingBase t = this.getThrower();
        double half = t == null ? 0.3 : t.width / 2 + 0.01, h = t == null ? 1.8 : t.height;
        EnumFacing f = result.sideHit;
        double x = result.hitVec.x + f.getXOffset() * half, z = result.hitVec.z + f.getZOffset() * half;
        double y = result.hitVec.y + (f == EnumFacing.DOWN ? -h - 0.01 : 0.01);
        this.setPosition(x, y, z);
    }
}
