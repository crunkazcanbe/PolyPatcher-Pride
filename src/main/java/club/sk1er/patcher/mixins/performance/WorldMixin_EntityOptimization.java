package club.sk1er.patcher.mixins.performance;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;
import java.util.List;

@Mixin(World.class)
public class WorldMixin_EntityOptimization {
    //#if MC==10809
    @Shadow @Final public boolean isRemote;

    @ModifyVariable(method = "updateEntityWithOptionalForce", at = @At("STORE"), ordinal = 1)
    private boolean patcher$checkIfWorldIsRemoteBeforeForceUpdating(boolean isForced) {
        return isForced && !this.isRemote;
    }
    //#endif

    /**
     * Pride Edition: skip only the entity-vs-entity box search, and only on the client world. The old early return
     * also skipped Forge's GetCollisionBoxesEvent (modded collision such as ships and trains) and changed server
     * physics in single player (items/TNT ignored boats and shulkers).
     */
    @Redirect(method = "getCollidingBoundingBoxes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;"))
    private List<Entity> patcher$filterEntities(World world, Entity entityIn, AxisAlignedBB bb) {
        if (world.isRemote && (entityIn instanceof EntityTNTPrimed || entityIn instanceof EntityFallingBlock || entityIn instanceof EntityItem
            // particles aren't entities after 1.9
            //#if MC==10809
            || entityIn instanceof EntityFX
            //#endif
        )) {
            return Collections.emptyList();
        }
        return world.getEntitiesWithinAABBExcludingEntity(entityIn, bb);
    }
}
