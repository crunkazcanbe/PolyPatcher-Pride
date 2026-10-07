package club.sk1er.patcher.util.world.render;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.server.MinecraftServer;

//#if MC==11202
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.server.integrated.IntegratedServer;
//#endif

public class FullbrightTicker {

    /**
     * Pride Edition: Celeritas (Embeddium) builds terrain from the raw light arrays, so Patcher's Fullbright never
     * brightens terrain there. It only switched off client light updates (placed torches stayed dark) and froze the
     * lightmap at its first frame. Smart Fullbright now turns it off when Celeritas is installed.
     */
    private static final boolean CELERITAS = FullbrightTicker.class.getClassLoader().getResource("org/taumc/celeritas/CeleritasVintage.class") != null;

    public static boolean isFullbright() {
        if (CELERITAS && PatcherConfig.smartFullbright) {
            return false;
        }

        //#if MC==10809
        MinecraftServer server = MinecraftServer.getServer();
        //#else
        //$$ IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
        //#endif
        if (server != null && server.isCallingFromMinecraftThread()) {
            return false;
        }

        return PatcherConfig.fullbright;
    }
}
