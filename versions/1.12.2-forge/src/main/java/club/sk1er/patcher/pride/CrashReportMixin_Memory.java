package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC-123545: the crash report's memory line lists FREE memory first and reads like "used" — add a clear line. */
@Mixin(CrashReport.class)
public class CrashReportMixin_Memory {
    @Shadow @Final private CrashReportCategory systemDetailsCategory;

    @Inject(method = "populateEnvironment", at = @At("TAIL"))
    private void pride$clearMemoryLine(CallbackInfo ci) {
        if (!PatcherConfig.prideCrashMemory) return;
        Runtime r = Runtime.getRuntime();
        long used = (r.totalMemory() - r.freeMemory()) >> 20, alloc = r.totalMemory() >> 20, max = r.maxMemory() >> 20;
        this.systemDetailsCategory.addDetail("Memory (clear)", () -> used + " MB used / " + alloc + " MB allocated / " + max + " MB max");
    }
}
