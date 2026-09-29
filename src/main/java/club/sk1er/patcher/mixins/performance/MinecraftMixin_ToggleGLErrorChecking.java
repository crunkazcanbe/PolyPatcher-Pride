package club.sk1er.patcher.mixins.performance;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin_ToggleGLErrorChecking {
    // Pride fork: on 1.12.2 this field is `private final boolean enableGLErrorChecking = true` — a compile-time
    // constant, so vanilla's reads are inlined and this write never did anything; on newer Java (Cleanroom) writing
    // a final field from outside the constructor throws IllegalAccessError at startup. Only apply it on 1.8.9.
    //#if MC==10809
    @Shadow private boolean enableGLErrorChecking;

    @Inject(method = "startGame", at = @At("TAIL"))
    private void patcher$disableGlErrorChecking(CallbackInfo ci) {
        this.enableGLErrorChecking = false;
    }
    //#endif
}
