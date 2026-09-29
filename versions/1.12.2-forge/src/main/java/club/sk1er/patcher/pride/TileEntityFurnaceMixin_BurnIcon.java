package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** MC-10025: after a reload the flame icon was sized from the NEXT fuel item instead of the one burning. Save the real one. */
@Mixin(TileEntityFurnace.class)
public class TileEntityFurnaceMixin_BurnIcon {
    @Shadow private int currentItemBurnTime;

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void pride$saveBurnTotal(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (PatcherConfig.prideFurnaceIcon) tag.setInteger("PrideBurnTotal", this.currentItemBurnTime);
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void pride$loadBurnTotal(NBTTagCompound tag, CallbackInfo ci) {
        if (PatcherConfig.prideFurnaceIcon && tag.hasKey("PrideBurnTotal")) this.currentItemBurnTime = tag.getInteger("PrideBurnTotal");
    }
}
