package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.LanServerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/** MC-110902: a LAN world that stopped stays in the multiplayer list forever. Worlds ping every 1.5s; drop any silent for 10s. */
@Mixin(targets = "net.minecraft.client.network.LanServerDetector$LanServerList")
public abstract class LanServerListMixin_Timeout {
    @Shadow @Final private List<LanServerInfo> listOfLanServers;
    @Shadow boolean wasUpdated;

    @Inject(method = "getWasUpdated", at = @At("HEAD"))
    private void pride$expire(CallbackInfoReturnable<Boolean> cir) {
        if (!PatcherConfig.prideLanTimeout) return;
        long now = Minecraft.getSystemTime();
        if (listOfLanServers.removeIf(s -> now - ((LanServerInfoAccessor) s).pride$lastSeen() > 10_000L)) wasUpdated = true;
    }
}
