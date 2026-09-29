package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC-96347: when a leash came off (e.g. the mob got into a boat) the fence knot stayed floating. Remove an empty knot. */
@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin_LeashKnot {
    @Shadow private Entity leashHolder;
    @Unique private Entity pride$oldHolder;

    @Inject(method = "clearLeashed", at = @At("HEAD"))
    private void pride$rememberKnot(boolean sendPacket, boolean dropLead, CallbackInfo ci) { this.pride$oldHolder = this.leashHolder; }

    @Inject(method = "clearLeashed", at = @At("TAIL"))
    private void pride$dropEmptyKnot(boolean sendPacket, boolean dropLead, CallbackInfo ci) {
        Entity knot = this.pride$oldHolder;
        this.pride$oldHolder = null;
        if (!PatcherConfig.prideLeashKnot || !(knot instanceof EntityLeashKnot) || knot.world.isRemote || knot.isDead) return;
        AxisAlignedBB area = knot.getEntityBoundingBox().grow(10.0D);
        for (EntityLiving e : knot.world.getEntitiesWithinAABB(EntityLiving.class, area))
            if (e.getLeashed() && e.getLeashHolder() == knot) return;                 // still tying something
        ((EntityHanging) knot).onBroken(null);
        knot.setDead();
    }
}
