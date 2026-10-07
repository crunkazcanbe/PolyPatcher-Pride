package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** MC-26304: which ingredient was brewing wasn't saved, so after a chunk reload the stand thought it changed and restarted. */
@Mixin(TileEntityBrewingStand.class)
public class TileEntityBrewingStandMixin_KeepProgress {
    @Shadow private int brewTime;
    @Shadow private Item ingredientID;
    @Shadow private NonNullList<ItemStack> brewingItemStacks;

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void pride$restoreIngredient(NBTTagCompound tag, CallbackInfo ci) {
        if (PatcherConfig.prideBrewingSave && this.brewTime > 0) this.ingredientID = this.brewingItemStacks.get(3).getItem();
    }
}
