package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** MC-1981 (part 1): remember the exact arrow item that was shot (its name, NBT, potion) on the arrow entity. */
@Mixin(ItemBow.class)
public class ItemBowMixin_ArrowStack {
    @Redirect(method = "onPlayerStoppedUsing", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemArrow;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/entity/projectile/EntityArrow;"))
    private EntityArrow pride$remember(ItemArrow item, World world, ItemStack stack, EntityLivingBase shooter) {
        EntityArrow arrow = item.createArrow(world, stack, shooter);
        if (PatcherConfig.prideArrowNbt && arrow != null && stack.hasTagCompound()) {        // plain arrows need nothing
            ItemStack one = stack.copy();
            one.setCount(1);
            arrow.getEntityData().setTag("PrideArrowStack", one.writeToNBT(new NBTTagCompound()));
        }
        return arrow;
    }
}
