package club.sk1er.patcher.util.world.sound.audioswitcher;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Replaces the {@code AL.create()} call in paulscode's LibraryLWJGLOpenAL#init.
 * <p>
 * Pride Edition: on LWJGL 3 (Cleanroom, lwjglx shim) the old destroy -> create -> destroy -> create dance
 * left OpenAL in a broken state ("Invalid enumerated parameter value", no sound at all), and the shim's
 * AL.create(String, ...) ignores the device name anyway. Now:
 * <ul>
 *     <li>No device selected (default): exactly vanilla, a single AL.create().</li>
 *     <li>Device selected on LWJGL 3: AL.create() on the default device, then move that same device to the
 *     selected output with ALC_SOFT_reopen_device. The context paulscode uses is never destroyed.</li>
 *     <li>Device selected on LWJGL 2: upstream behaviour (AL.create(name, ...) works there).</li>
 * </ul>
 * Switching devices in the sound options triggers a full SoundManager reload (see AudioSwitcher), which
 * lands back here.
 */
@SuppressWarnings("unused")
public class LibraryLWJGLOpenALImpl {
    public static void createAL() throws LWJGLException {
        String selected = PatcherConfig.selectedAudioDevice;
        if (selected == null || selected.isEmpty()) {
            AL.create();
            return;
        }

        if (ALCHelper.isLWJGL3()) {
            AL.create();
            try {
                if (!ALCHelper.getAvailableDevicesLWJGL3().contains(selected)) {
                    Patcher.instance.getLogger().warn("Audio device '{}' not found, using the system default.", selected);
                } else if (!reopenDevice(selected)) {
                    Patcher.instance.getLogger().warn("Could not switch to audio device '{}', using the system default.", selected);
                }
            } catch (Throwable e) {
                Patcher.instance.getLogger().error("Failed to switch audio device, using the system default.", e);
            }
            return;
        }

        // LWJGL 2 (upstream path)
        try {
            if (AL.isCreated()) AL.destroy();

            AudioSwitcher audioSwitcher = Patcher.instance.getAudioSwitcher();
            List<String> devices = audioSwitcher.getDevices();
            if (devices.isEmpty()) {
                AL.create();
                audioSwitcher.fetchAvailableDevicesUncached();
                devices = audioSwitcher.getDevices();
                AL.destroy();
            }

            if (devices.contains(selected)) {
                AL.create(selected, 44100, 60, false);
            } else {
                AL.create();
            }
        } catch (Throwable e) {
            Patcher.instance.getLogger().error("Failed to create device, using system default.", e);
            if (AL.isCreated()) AL.destroy();
            AL.create();
        }
    }

    /** LWJGL 3 only: ALC_SOFT_reopen_device on the device the shim just opened. */
    private static boolean reopenDevice(String name) throws Exception {
        Object alcDevice = AL.getDevice();
        if (alcDevice == null) return false;
        Field field = alcDevice.getClass().getDeclaredField("device");
        field.setAccessible(true);
        long handle = field.getLong(alcDevice);

        Class<?> alc10 = Class.forName("org.lwjgl.openal.ALC10");
        boolean supported = (boolean) alc10.getMethod("alcIsExtensionPresent", long.class, CharSequence.class)
            .invoke(null, handle, "ALC_SOFT_reopen_device");
        if (!supported) return false;

        Class<?> reopen = Class.forName("org.lwjgl.openal.SOFTReopenDevice");
        return (boolean) reopen.getMethod("alcReopenDeviceSOFT", long.class, CharSequence.class, int[].class)
            .invoke(null, handle, name, hrtfAttributes());
    }

    /** Keep Cleanroom's "disable HRTF" choice when reopening (ALC_HRTF_SOFT / ALC_HRTF_ID_SOFT = 0). */
    private static int[] hrtfAttributes() {
        try {
            Class<?> early = Class.forName("net.minecraftforge.common.ForgeEarlyConfig");
            Object category = early.getField("OPENAL_CONTEXT").get(null);
            if (category.getClass().getField("ENABLE_HRTF").getBoolean(category)) return null;
            return new int[]{0x1992, 0, 0x1996, 0, 0};
        } catch (Throwable ignored) {
            return null;
        }
    }
}
