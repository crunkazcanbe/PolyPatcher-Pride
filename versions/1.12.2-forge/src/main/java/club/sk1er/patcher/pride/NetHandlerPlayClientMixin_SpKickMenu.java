package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** MC-45602: being kicked from a SINGLE-player world showed the multiplayer menu afterwards. Go to the title screen. */
@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin_SpKickMenu {
    @ModifyArg(method = "onDisconnect", index = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiDisconnected;<init>(Lnet/minecraft/client/gui/GuiScreen;Ljava/lang/String;Lnet/minecraft/util/text/ITextComponent;)V"))
    private GuiScreen pride$titleAfterSpKick(GuiScreen parent) {
        return PatcherConfig.prideSpKickMenu && Minecraft.getMinecraft().isSingleplayer() ? new GuiMainMenu() : parent;
    }
}
