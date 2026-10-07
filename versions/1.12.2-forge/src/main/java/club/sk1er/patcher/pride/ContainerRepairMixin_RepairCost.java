package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** MC-125046: renaming wrote "RepairCost:0" onto items, so renamed items no longer stacked with plain ones. */
@Mixin(ContainerRepair.class)
public class ContainerRepairMixin_RepairCost {
    @Redirect(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V"))
    private void pride$noZeroRepairCost(ItemStack stack, int cost) {
        if (PatcherConfig.prideRepairCost && cost <= 0 && (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("RepairCost"))) return;
        stack.setRepairCost(cost);
    }
}
