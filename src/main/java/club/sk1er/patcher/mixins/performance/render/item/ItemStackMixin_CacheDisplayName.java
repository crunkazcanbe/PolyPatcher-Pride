package club.sk1er.patcher.mixins.performance.render.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Pride Edition: the cache is only used while the stack has no NBT and still has the same item and damage it had
 * when the name was cached. Upstream cached forever and only reset on setStackDisplayName, so names went stale when
 * a mod changed the NBT (fluids, custom names cleared, energy/mode names) or the damage/subtype in place.
 */
@Mixin(ItemStack.class)
public class ItemStackMixin_CacheDisplayName {
    @Unique private String patcher$cachedDisplayName;
    @Unique private Item patcher$cachedItem;
    @Unique private int patcher$cachedDamage;

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void patcher$returnCachedDisplayName(CallbackInfoReturnable<String> cir) {
        String cached = patcher$cachedDisplayName;
        if (cached != null) {
            ItemStack stack = (ItemStack) (Object) this;
            if (stack.getTagCompound() == null && stack.getItem() == patcher$cachedItem && stack.getItemDamage() == patcher$cachedDamage) {
                cir.setReturnValue(cached);
            } else {
                patcher$cachedDisplayName = null;
            }
        }
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"))
    private void patcher$cacheDisplayName(CallbackInfoReturnable<String> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getTagCompound() != null) return;
        patcher$cachedItem = stack.getItem();
        patcher$cachedDamage = stack.getItemDamage();
        patcher$cachedDisplayName = cir.getReturnValue();
    }

    @Inject(method = "setStackDisplayName", at = @At("HEAD"))
    private void patcher$resetCachedDisplayName(String displayName, CallbackInfoReturnable<ItemStack> cir) {
        patcher$cachedDisplayName = null;
    }
}
