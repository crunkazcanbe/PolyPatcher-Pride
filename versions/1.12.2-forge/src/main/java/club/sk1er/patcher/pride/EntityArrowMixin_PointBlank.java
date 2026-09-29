package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * MC-125936: an arrow that starts inside a mob's hitbox (shooting point blank) flies straight through it, because the
 * ray never crosses the box's edge. On an arrow's first tick only, starting inside counts as a hit — never on your own
 * mount or a fellow passenger, and never after a bounce (a shield bounce would otherwise re-hit every tick).
 */
@Mixin(EntityArrow.class)
public abstract class EntityArrowMixin_PointBlank extends Entity {
    @Shadow public Entity shootingEntity;
    @Unique private Entity pride$candidate;

    public EntityArrowMixin_PointBlank(World w) { super(w); }

    @Redirect(method = "findEntityOnPath", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object pride$remember(List<?> list, int i) {
        Object e = list.get(i);
        pride$candidate = (Entity) e;
        return e;
    }

    @Redirect(method = "findEntityOnPath", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;calculateIntercept(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"))
    private RayTraceResult pride$pointBlank(AxisAlignedBB box, Vec3d start, Vec3d end) {
        RayTraceResult r = box.calculateIntercept(start, end);
        if (r != null || !PatcherConfig.prideArrowPointBlank || this.ticksExisted > 1 || !box.contains(start)) return r;
        Entity e = pride$candidate;
        if (e == null || e == shootingEntity || (shootingEntity != null && shootingEntity.isRidingSameEntity(e))) return null;
        return new RayTraceResult(start, EnumFacing.UP);   // only hitVec is read: distance 0 = the closest hit
    }
}
