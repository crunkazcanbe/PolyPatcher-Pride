package club.sk1er.patcher.pride;

import club.sk1er.patcher.config.PatcherConfig;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

/**
 * MC-92867: a null text value (books, signs, commands, "/tellraw @a null") threw or came through as null and crashed the
 * first mod that read the chat line — treat it as empty text. Gson returns a bare top-level null WITHOUT calling
 * deserialize, so the two public entry points are guarded too (found in the pack test 2026-09-29).
 */
@Mixin(ITextComponent.Serializer.class)
public class ITextComponentSerializerMixin_NullText {
    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/util/text/ITextComponent;",
            at = @At("HEAD"), cancellable = true)
    private void pride$nullIsEmpty(JsonElement json, Type type, JsonDeserializationContext ctx, CallbackInfoReturnable<ITextComponent> cir) {
        if (PatcherConfig.prideNullJsonText && (json == null || json.isJsonNull())) cir.setReturnValue(new TextComponentString(""));
    }

    @Inject(method = {"jsonToComponent", "fromJsonLenient"}, at = @At("RETURN"), cancellable = true)
    private static void pride$neverNull(String json, CallbackInfoReturnable<ITextComponent> cir) {
        if (PatcherConfig.prideNullJsonText && cir.getReturnValue() == null) cir.setReturnValue(new TextComponentString(""));
    }
}
