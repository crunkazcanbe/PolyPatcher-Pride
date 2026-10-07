package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC-128561: CommandBase kept the stopped server's command manager (and so the whole old world) in memory. */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin_CommandLeak {
    @Inject(method = "stopServer", at = @At("TAIL"))
    private void pride$forgetOldServer(CallbackInfo ci) {
        if (PatcherConfig.prideCommandLeak) CommandBase.setCommandListener(null);
    }
}
