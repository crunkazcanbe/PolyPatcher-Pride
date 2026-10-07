package club.sk1er.patcher.pride;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** registers the Pride fixes that are events, not mixins (dragon egg tab, cooked drops) once the game has started */
@Mixin(Minecraft.class)
public class MinecraftMixin_PrideInit {
    @Inject(method = "init", at = @At("TAIL"))
    private void pride$init(CallbackInfo ci) { club.sk1er.patcher.pridefix.PrideFixes.init(); }
}
