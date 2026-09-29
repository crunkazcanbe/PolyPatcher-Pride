package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** MC-58961: a long kick message ran under the Back button (the button is pinned at height-30). Lift the text above it. */
@Mixin(GuiDisconnected.class)
public abstract class GuiDisconnectedMixin_Overlap extends GuiScreen {
    @Shadow private int textHeight;

    private int pride$top() {
        int top = this.height / 2 - this.textHeight / 2;
        if (!PatcherConfig.prideKickOverlap) return top;
        return Math.max(this.fontRenderer.FONT_HEIGHT * 3, Math.min(top, this.height - 34 - this.textHeight));
    }

    @ModifyVariable(method = "drawScreen", at = @At(value = "STORE"), ordinal = 2)
    private int pride$textTop(int i) { return pride$top(); }

    @ModifyArg(method = "drawScreen", index = 3, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/client/gui/GuiDisconnected;drawCenteredString(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)V"))
    private int pride$reasonY(int y) { return pride$top() - this.fontRenderer.FONT_HEIGHT * 2; }
}
