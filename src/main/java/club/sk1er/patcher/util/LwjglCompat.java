package club.sk1er.patcher.util;

import java.lang.reflect.Method;

/**
 * Pride Edition: Cleanroom runs Minecraft on LWJGL 3 behind an LWJGL 2 shim (lwjglx). The shim merges its methods
 * into the real org.lwjgl.* classes, so LWJGL 2 calls still link, but a few of them behave differently
 * (OpenAL device selection, window modes). Code that depends on those checks here first.
 */
public final class LwjglCompat {
    private static Boolean lwjgl3;

    private LwjglCompat() {
    }

    /** True on Cleanroom / any LWJGL 3 runtime (GLFW present), false on stock LWJGL 2. */
    public static boolean isLWJGL3() {
        if (lwjgl3 == null) {
            try {
                Class.forName("org.lwjgl.glfw.GLFW", false, LwjglCompat.class.getClassLoader());
                lwjgl3 = true;
            } catch (Throwable t) {
                lwjgl3 = false;
            }
        }
        return lwjgl3;
    }

    /** Calls a public static method of a runtime class reflectively, returns null if it is missing or fails. */
    public static Object invokeStatic(String className, String method, Class<?>[] types, Object... args) {
        try {
            Method m = Class.forName(className).getMethod(method, types);
            return m.invoke(null, args);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Reads a public static boolean of Cleanroom's ForgeEarlyConfig, or the fallback when it does not exist. */
    public static boolean earlyConfigFlag(String field, boolean fallback) {
        try {
            return Class.forName("net.minecraftforge.common.ForgeEarlyConfig").getField(field).getBoolean(null);
        } catch (Throwable t) {
            return fallback;
        }
    }
}
