package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiSlot;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC-119901: the Controls list scrolled half a row per wheel notch — painfully slow with hundreds of mod keys. */
@Mixin(GuiSlot.class)
public class GuiSlotMixin_ControlsScroll {
    @Shadow protected float amountScrolled;
    @Shadow protected int slotHeight;

    @Inject(method = "handleMouseInput", at = @At("TAIL"))
    private void pride$fasterControls(CallbackInfo ci) {
        if (!PatcherConfig.prideControlsScroll || !((Object) this instanceof GuiKeyBindingList)) return;
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) this.amountScrolled += (wheel > 0 ? -1 : 1) * this.slotHeight * 1.5f;   // 2 rows per notch total
    }
}
