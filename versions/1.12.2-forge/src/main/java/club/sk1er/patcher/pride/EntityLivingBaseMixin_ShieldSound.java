package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** MC-105068: a hit fully blocked by a shield still played the "ouch" hurt sound on top of the shield clunk. */
@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin_ShieldSound {
    @Unique private boolean pride$blocked;

    @Shadow protected abstract void playHurtSound(DamageSource source);
    @Shadow private boolean canBlockDamageSource(DamageSource source) { return false; }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void pride$reset(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) { pride$blocked = false; }

    @Redirect(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;canBlockDamageSource(Lnet/minecraft/util/DamageSource;)Z"))
    private boolean pride$noteBlock(EntityLivingBase self, DamageSource source) {
        return pride$blocked = canBlockDamageSource(source);   // true = the shield took it all (vanilla sets the damage to 0)
    }

    @Redirect(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;playHurtSound(Lnet/minecraft/util/DamageSource;)V"))
    private void pride$quietWhenBlocked(EntityLivingBase self, DamageSource source) {
        if (!(pride$blocked && PatcherConfig.prideShieldSound)) playHurtSound(source);
    }
}
