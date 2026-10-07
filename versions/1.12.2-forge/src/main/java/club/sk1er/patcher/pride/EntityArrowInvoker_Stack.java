package club.sk1er.patcher.pride;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityArrow.class)
public interface EntityArrowInvoker_Stack {
    @Invoker("getArrowStack")
    ItemStack pride$getArrowStack();
}
