package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC-57057: the guardian laser sound had NO distance falloff, so you heard it at full volume anywhere. */
@Mixin(GuardianSound.class)
public abstract class GuardianSoundMixin_Distance extends MovingSound {
    protected GuardianSoundMixin_Distance(SoundEvent s, SoundCategory c) { super(s, c); }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void pride$fadeWithDistance(EntityGuardian guardian, CallbackInfo ci) {
        if (PatcherConfig.prideGuardianSound) this.attenuationType = ISound.AttenuationType.LINEAR;
    }
}
