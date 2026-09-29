package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC-35860: a record in a jukebox didn't stop the background music — both played over each other. */
@Mixin(MusicTicker.class)
public class MusicTickerMixin_Records {
    @Shadow @Final private Minecraft mc;
    @Shadow private ISound currentMusic;
    @Shadow private int timeUntilNextMusic;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void pride$quietWhileRecordPlays(CallbackInfo ci) {
        if (!PatcherConfig.prideJukeboxMusic || this.mc.renderGlobal == null) return;
        boolean record = false;
        for (ISound s : ((RenderGlobalAccessor_Records) this.mc.renderGlobal).pride$records().values())
            if (this.mc.getSoundHandler().isSoundPlaying(s)) { record = true; break; }
        if (!record) return;
        if (this.currentMusic != null) { this.mc.getSoundHandler().stopSound(this.currentMusic); this.currentMusic = null; }
        this.timeUntilNextMusic = Math.max(this.timeUntilNextMusic, 200);   // a pause after the record, then music again
        ci.cancel();
    }
}
