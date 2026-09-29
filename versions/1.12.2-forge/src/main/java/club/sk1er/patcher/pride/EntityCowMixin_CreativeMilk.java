package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** MC-36322: cows couldn't be milked in creative. Like newer versions: you get a milk bucket, the bucket stays. */
@Mixin(EntityCow.class)
public abstract class EntityCowMixin_CreativeMilk {
    @Inject(method = "processInteract", at = @At("HEAD"), cancellable = true)
    private void pride$creativeMilk(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        EntityCow self = (EntityCow) (Object) this;
        if (!PatcherConfig.prideCreativeMilk || !player.capabilities.isCreativeMode || self.isChild()) return;
        if (player.getHeldItem(hand).getItem() != Items.BUCKET) return;
        player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
        if (!player.inventory.hasItemStack(new ItemStack(Items.MILK_BUCKET))) player.inventory.addItemStackToInventory(new ItemStack(Items.MILK_BUCKET));
        cir.setReturnValue(true);
    }
}
