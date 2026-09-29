package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** MC-118757: a huge rain/thunder level from the server made the game lag and warped the screen. Keep it 0..1. */
@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin_RainClamp {
    @Redirect(method = "handleChangeGameState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setRainStrength(F)V"))
    private void pride$clampRain(World w, float f) { w.setRainStrength(PatcherConfig.prideRainClamp ? Math.max(0f, Math.min(1f, f)) : f); }

    @Redirect(method = "handleChangeGameState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setThunderStrength(F)V"))
    private void pride$clampThunder(World w, float f) { w.setThunderStrength(PatcherConfig.prideRainClamp ? Math.max(0f, Math.min(1f, f)) : f); }
}
