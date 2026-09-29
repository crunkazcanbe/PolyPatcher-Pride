package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.WeightedRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Random;

/** MC-89880: a spawner whose list only has weight-0 entries crashed (WeightedRandom with total weight 0). */
@Mixin(MobSpawnerBaseLogic.class)
public class MobSpawnerBaseLogicMixin_ZeroWeight {
    @Redirect(method = {"updateSpawner", "readFromNBT"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/WeightedRandom;getRandomItem(Ljava/util/Random;Ljava/util/List;)Lnet/minecraft/util/WeightedRandom$Item;"))
    private WeightedRandom.Item pride$safePick(Random rand, List<? extends WeightedRandom.Item> list) {
        if (PatcherConfig.prideSpawnerZeroWeight && WeightedRandom.getTotalWeight(list) <= 0) return list.get(0);
        return WeightedRandom.getRandomItem(rand, list);
    }
}
