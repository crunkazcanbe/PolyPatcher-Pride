package club.sk1er.patcher.pride;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/** lets the music ticker see which jukeboxes are playing (MC-35860) */
@Mixin(RenderGlobal.class)
public interface RenderGlobalAccessor_Records {
    @Accessor("mapSoundPositions")
    Map<BlockPos, ISound> pride$records();
}
