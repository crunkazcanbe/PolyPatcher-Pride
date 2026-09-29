package club.sk1er.patcher.pride;

import net.minecraft.client.network.LanServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LanServerInfo.class)
public interface LanServerInfoAccessor {
    @Accessor("timeLastSeen") long pride$lastSeen();
}
