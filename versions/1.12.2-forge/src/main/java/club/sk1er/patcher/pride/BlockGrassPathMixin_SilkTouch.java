package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/** MC-106428: dirt paths dropped dirt even with Silk Touch (not a full cube, so vanilla refused silk harvest). */
@Mixin(BlockGrassPath.class)
public abstract class BlockGrassPathMixin_SilkTouch extends Block {
    public BlockGrassPathMixin_SilkTouch(Material m) { super(m); }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return PatcherConfig.pridePathSilk || super.canSilkHarvest(world, pos, state, player);
    }
}
