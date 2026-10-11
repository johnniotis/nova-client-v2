package dev.nova;

import com.mojang.blaze3d.platform.InputConstants;
import dev.nova.gui.ClickGuiScreen;
import dev.nova.module.ModuleManager;
import dev.nova.module.modules.EntityESP;
import dev.nova.module.modules.Freecam;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class NovaClient implements ClientModInitializer {
    public static final String MOD_ID = "novaclient";
    private static final ModuleManager MODULES = new ModuleManager();
    private static KeyBinding guiKey;

    public static ModuleManager modules() { return MODULES; }

    @Override
    public void onInitializeClient() {
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.novaclient.gui",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.novaclient.main"
        ));

        MODULES.register(new Freecam(GLFW.GLFW_KEY_F6));
        MODULES.register(new EntityESP(GLFW.GLFW_KEY_G));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MODULES.tick(client);
            while (guiKey.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new ClickGuiScreen());
            }
        });
    }
}
