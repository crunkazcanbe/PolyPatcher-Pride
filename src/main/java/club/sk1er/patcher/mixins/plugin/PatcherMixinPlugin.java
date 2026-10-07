package club.sk1er.patcher.mixins.plugin;

import club.sk1er.patcher.tweaker.ClassTransformer;
import com.google.common.collect.ArrayListMultimap;
import kotlin.text.StringsKt;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class PatcherMixinPlugin implements IMixinConfigPlugin {
    private static final String LABYMOD_CLASS = "net/labymod/main/LabyMod.class";
    private static final String SMOOTHFONT_CLASS = "bre/smoothfont/mod_SmoothFont.class";
    private static final String OF_CONFIG_CLASS = "Config.class";

    private static final ArrayListMultimap<String, String> CONFLICTING_CLASSES = ArrayListMultimap.create();
    private static boolean isEarsMod = false;

    static {
        MixinEnvironment.getDefaultEnvironment().addTransformerExclusion("com.unascribed.ears.asm.PlatformTransformerAdapter");
        CONFLICTING_CLASSES.put("GuiContainerMixin_MouseBindFixThatLabyBreaks", LABYMOD_CLASS);
        CONFLICTING_CLASSES.put("FontRendererMixin_Optimization", SMOOTHFONT_CLASS);
        CONFLICTING_CLASSES.put("MathHelperMixin_CompactLUT", OF_CONFIG_CLASS);
        // Pride fork (2026-09-29, found in a 450-mod pack): these features collide with mods that rewrite the same
        // vanilla method. Skip them cleanly instead of failing on every launch. LoliASM (also shipped as "chibi") already
        // handles the task-exception logging and has its own faster VisGraph flood fill; Valkyrien Skies rewrites the
        // random display-tick loop, so there's no constant left to lower.
        CONFLICTING_CLASSES.put("UtilMixin_StopLogSpam", "zone/rong/loliasm/common/crashes/mixins/UtilMixin.class");
        CONFLICTING_CLASSES.put("VisGraphMixin_LimitScan", "zone/rong/loliasm/client/rendering/mixins/VisGraphMixin.class");
        // its partner casts every VisGraph to VisGraphExt: skipping only the VisGraph half crashed the first frame (2026-09-29)
        CONFLICTING_CLASSES.put("RenderGlobalMixin_LimitVisGraphScan", "zone/rong/loliasm/client/rendering/mixins/VisGraphMixin.class");
        // LoliASM strips SoundRegistry's map; SoundHandler already reads RegistrySimple instead when LoliASM is present
        CONFLICTING_CLASSES.put("SoundRegistryAccessor", "zone/rong/loliasm/common/registries/mixins/SoundRegistryMixin.class");
        CONFLICTING_CLASSES.put("WorldClientMixin_AnimationTick", "org/valkyrienskies/mixin/client/multiplayer/MixinWorldClient.class");
    }

    @Override
    public void onLoad(String mixinPackage) {
        try {
            Class.forName("com.unascribed.ears.Ears", false, getClass().getClassLoader());
            isEarsMod = true;
        } catch (ClassNotFoundException ignored) {
            isEarsMod = false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("_EarsMod")) {
            return isEarsMod;
        }
        String mixinPackage = StringsKt.substringBeforeLast(mixinClassName, '.', mixinClassName);
        if (mixinPackage.endsWith("optifine") && "NONE".equals(ClassTransformer.optifineVersion)) {
            // OptiFine isn't present, let's not apply this
            return false;
        }
        for (String conflictingClass : CONFLICTING_CLASSES.get(StringsKt.substringAfterLast(mixinClassName, '.', mixinClassName))) {
            if (this.getClass().getClassLoader().getResource(conflictingClass) != null) {
                // Conflicting class is present, let's not apply this
                return false;
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, org.spongepowered.asm.lib.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, org.spongepowered.asm.lib.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
