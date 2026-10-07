package club.sk1er.patcher.tweaker;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Computes common super classes without ever loading (defining) a class: loading from inside a transformer is what
 * causes Mixin "Re-entrance error"s on Cleanroom.
 * <p>
 * Pride Edition: upstream called ClassInfo.getCommonSuperClass(...).getName() bare; for a class Mixin can't find that
 * threw an NPE, the transformer swallowed it and returned a half-written class. Now a failure falls back to reading
 * the raw .class resources (deobf-mapped) and walking super names, and finally to java/lang/Object (also what ASM and
 * Mixin use for interfaces).
 */
public class PatcherClassWriter extends ClassWriter {
    public PatcherClassWriter(int flags) {
        super(flags);
    }

    public PatcherClassWriter(ClassReader classReader, int flags) {
        super(classReader, flags);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        if (type1.equals(type2)) return type1;
        try {
            // upstream path: Mixin's ClassInfo reads bytecode through the transformer chain (never defines a class)
            ClassInfo info = ClassInfo.getCommonSuperClass(type1, type2);
            if (info != null) return info.getName();
        } catch (Throwable ignored) {
            // unknown class (ClassInfo NPE) or a loader that refuses re-entry: use the raw resource walk below
        }
        List<String> chain1 = superChain(type1);
        List<String> chain2 = superChain(type2);
        if (chain1 == null || chain2 == null) return "java/lang/Object";
        if (chain2.contains(type1)) return type1;
        if (chain1.contains(type2)) return type2;
        for (String s : chain1) {
            if (chain2.contains(s)) return s;
        }
        return "java/lang/Object";
    }

    /** type itself plus all super classes up to Object; null if a class is an interface or can't be read. */
    private static List<String> superChain(String type) {
        List<String> chain = new ArrayList<>();
        String current = type;
        while (current != null) {
            chain.add(current);
            if (current.equals("java/lang/Object")) return chain;
            ClassReader reader = read(current);
            if (reader == null || (reader.getAccess() & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0) return null;
            current = reader.getSuperName() == null ? null : FMLDeobfuscatingRemapper.INSTANCE.map(reader.getSuperName());
        }
        return chain;
    }

    private static ClassReader read(String internalName) {
        // production Minecraft classes are stored under their obfuscated names
        String resource = FMLDeobfuscatingRemapper.INSTANCE.unmap(internalName) + ".class";
        ClassLoader loader = Launch.classLoader != null ? Launch.classLoader : PatcherClassWriter.class.getClassLoader();
        try (InputStream in = loader.getResourceAsStream(resource)) {
            if (in != null) return new ClassReader(in);
        } catch (Throwable ignored) {
        }
        try (InputStream in = ClassLoader.getSystemResourceAsStream(resource)) {
            return in == null ? null : new ClassReader(in);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
