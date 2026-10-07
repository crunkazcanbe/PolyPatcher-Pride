package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** MC-1981 (part 2): picking the arrow back up gives the SAME item (name + NBT), not a fresh plain arrow. */
@Mixin(EntityArrow.class)
public class EntityArrowMixin_KeepStack {
    @Redirect(method = {"onCollideWithPlayer", "onHit"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/projectile/EntityArrow;getArrowStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack pride$sameArrow(EntityArrow self) {
        NBTTagCompound data = self.getEntityData();
        if (PatcherConfig.prideArrowNbt && data.hasKey("PrideArrowStack", 10)) {
            ItemStack s = new ItemStack(data.getCompoundTag("PrideArrowStack"));
            if (!s.isEmpty()) return s;
        }
        return ((EntityArrowInvoker_Stack) self).pride$getArrowStack();
    }
}
