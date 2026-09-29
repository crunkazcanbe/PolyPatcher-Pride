package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC-14923: the owner could be kicked for spamming in their own single-player world. MC-114544: sleeping = "flying" kick. */
@Mixin(NetHandlerPlayServer.class)
public class NetHandlerPlayServerMixin_Kicks {
    @Shadow public EntityPlayerMP player;
    @Shadow @Final private MinecraftServer server;
    @Shadow private boolean floating;
    @Shadow private int floatingTickCount;

    @Redirect(method = "processChatMessage", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/management/PlayerList;canSendCommands(Lcom/mojang/authlib/GameProfile;)Z"))
    private boolean pride$ownerNeverSpamKicked(PlayerList list, GameProfile profile) {
        if (PatcherConfig.prideSpSpam && this.server.isSinglePlayer() && profile.getName().equals(this.server.getServerOwner())) return true;
        return list.canSendCommands(profile);
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void pride$sleepingIsNotFlying(CallbackInfo ci) {
        if (PatcherConfig.prideSleepFlyKick && this.player.isPlayerSleeping()) { this.floating = false; this.floatingTickCount = 0; }
    }
}
