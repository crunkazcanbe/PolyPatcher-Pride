package club.sk1er.patcher.pridefix;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Pride fixes that are Forge events (not mixins). Kept OUTSIDE the mixin package (Mixin forbids normal classes there). */
public final class PrideFixes {
    private static boolean done;

    public static void init() {
        if (done) return;
        done = true;
        if (PatcherConfig.prideDragonEggTab && Blocks.DRAGON_EGG.getCreativeTab() == null)       // MC-55718
            Blocks.DRAGON_EGG.setCreativeTab(CreativeTabs.DECORATIONS);
        MinecraftForge.EVENT_BUS.register(new PrideFixes());
    }

    /** MC-70738 + MC-102269: guardians and polar bears killed by fire dropped RAW fish — smelt the drops like other mobs */
    @SubscribeEvent
    public void cookedDrops(LivingDropsEvent e) {
        if (!PatcherConfig.prideCookedDrops || !e.getEntityLiving().isBurning()) return;
        if (!(e.getEntityLiving() instanceof EntityGuardian) && !(e.getEntityLiving() instanceof EntityPolarBear)) return;
        for (EntityItem drop : e.getDrops()) {
            ItemStack raw = drop.getItem();
            if (!(raw.getItem() instanceof net.minecraft.item.ItemFishFood)) continue;
            ItemStack cooked = FurnaceRecipes.instance().getSmeltingResult(raw);
            if (!cooked.isEmpty()) { ItemStack c = cooked.copy(); c.setCount(raw.getCount()); drop.setItem(c); }
        }
    }
}
