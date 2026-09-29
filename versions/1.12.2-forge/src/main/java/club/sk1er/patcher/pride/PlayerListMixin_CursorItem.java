package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC-18216: the item held on the mouse was lost on logout / server stop. Put it back before the player is saved. */
@Mixin(PlayerList.class)
public class PlayerListMixin_CursorItem {
    @Inject(method = "playerLoggedOut", at = @At("HEAD"))
    private void pride$keepCursorItem(EntityPlayerMP player, CallbackInfo ci) {
        if (!PatcherConfig.prideCursorItem) return;
        ItemStack held = player.inventory.getItemStack();
        if (held.isEmpty()) return;
        player.inventory.setItemStack(ItemStack.EMPTY);
        if (!player.inventory.addItemStackToInventory(held)) player.dropItem(held, false);
    }
}
