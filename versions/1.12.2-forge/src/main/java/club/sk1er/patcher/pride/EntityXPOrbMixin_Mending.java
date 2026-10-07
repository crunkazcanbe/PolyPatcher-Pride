package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

/** MC-92889: Mending picked a random mending item, even a fully repaired one, and the XP was wasted. Pick a DAMAGED one. */
@Mixin(EntityXPOrb.class)
public class EntityXPOrbMixin_Mending {
    @Redirect(method = "onCollideWithPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEnchantedItem(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack pride$damagedMendingItem(Enchantment ench, EntityLivingBase entity) {
        if (!PatcherConfig.prideMendingXp) return EnchantmentHelper.getEnchantedItem(ench, entity);
        List<ItemStack> damaged = new ArrayList<>();
        for (ItemStack s : ench.getEntityEquipment(entity))
            if (!s.isEmpty() && s.isItemDamaged() && EnchantmentHelper.getEnchantmentLevel(ench, s) > 0) damaged.add(s);
        return damaged.isEmpty() ? ItemStack.EMPTY : damaged.get(entity.getRNG().nextInt(damaged.size()));
    }
}
