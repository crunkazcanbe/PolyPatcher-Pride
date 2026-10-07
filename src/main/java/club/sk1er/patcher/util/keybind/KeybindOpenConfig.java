package club.sk1er.patcher.util.keybind;

import club.sk1er.patcher.Patcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

/**
 * Pride Edition: a normal Minecraft key binding (Controls > Patcher) that opens the PolyPatcher settings.
 * OneConfig's own hotkey never fires on Cleanroom; vanilla key bindings always do. Unbound by default.
 */
public class KeybindOpenConfig extends KeyBinding {

    public KeybindOpenConfig() {
        super("Open PolyPatcher Settings", Keyboard.KEY_NONE, "Patcher");
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        boolean pressed = false;
        while (this.isPressed()) pressed = true;
        if (pressed && Minecraft.getMinecraft().currentScreen == null) {
            Patcher.instance.getPatcherConfig().openGui();
        }
    }
}
