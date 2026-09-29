package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * MC-3587: closing a container with an item on the cursor DROPPED it — e.g. an anvil breaking on its last use threw
 * your finished item on the floor. Newer versions put it back into your inventory (drop only what doesn't fit).
 */
@Mixin(Container.class)
public class ContainerMixin_CursorToInventory {
    @Redirect(method = "onContainerClosed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayer;dropItem(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/item/EntityItem;"))
    private EntityItem pride$backIntoInventory(EntityPlayer player, ItemStack stack, boolean unused) {
        if (!PatcherConfig.prideAnvilLastUse) return player.dropItem(stack, unused);
        if (player.world.isRemote) return null;                         // the server decides; it syncs the slots back
        ItemStack rest = stack.copy();
        player.inventory.addItemStackToInventory(rest);
        return rest.isEmpty() ? null : player.dropItem(rest, false);
    }
}
