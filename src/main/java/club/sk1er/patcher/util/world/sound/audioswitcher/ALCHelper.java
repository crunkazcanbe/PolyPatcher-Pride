package club.sk1er.patcher.util.world.sound.audioswitcher;

import club.sk1er.patcher.util.LwjglCompat;
import org.lwjgl.openal.ALC10;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ALCHelper {

    private static final int ALC_DEVICE_SPECIFIER = 0x1005;
    private static final int ALC_ALL_DEVICES_SPECIFIER = 0x1013;
    private List<String> devices = new ArrayList<>();

    public static boolean isLWJGL3() {
        return LwjglCompat.isLWJGL3();
    }

    /**
     * LWJGL 3: list every output device without touching any device or context.
     * alcGetString through the shim only returns the first entry of the list (and uses the open device,
     * which makes ALC_ALL_DEVICES_SPECIFIER an invalid enum), so use ALUtil.getStringList(NULL, ...).
     */
    public static List<String> getAvailableDevicesLWJGL3() throws Exception {
        Class<?> alc10 = Class.forName("org.lwjgl.openal.ALC10");
        boolean all = (boolean) alc10.getMethod("alcIsExtensionPresent", long.class, CharSequence.class)
            .invoke(null, 0L, "ALC_ENUMERATE_ALL_EXT");
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) Class.forName("org.lwjgl.openal.ALUtil")
            .getMethod("getStringList", long.class, int.class)
            .invoke(null, 0L, all ? ALC_ALL_DEVICES_SPECIFIER : ALC_DEVICE_SPECIFIER);
        return list == null ? Collections.emptyList() : list;
    }

    public List<String> getAvailableDevices(boolean useCache) {
        if (!useCache || this.devices.isEmpty()) {
            String[] availableDevices = this.getAvailableDevicesString();

            if (availableDevices == null) {
                this.devices = new ArrayList<>();
            } else {
                this.devices = Arrays.stream(availableDevices).filter(s -> !s.isEmpty()).distinct().collect(Collectors.toList());
            }
        }

        return this.devices;
    }

    @Nullable
    private String[] getAvailableDevicesString() {
        if (isLWJGL3()) {
            try {
                return getAvailableDevicesLWJGL3().toArray(new String[0]);
            } catch (Throwable ignored) {
                return null;
            }
        }

        try {
            return ALC10.alcGetString(null, ALC_ALL_DEVICES_SPECIFIER).split("\0");
        } catch (Throwable ignored) {
            try {
                return ALC10.alcGetString(null, ALC_DEVICE_SPECIFIER).split("\0");
            } catch (Throwable ignored2) {
                return null;
            }
        }
    }
}
