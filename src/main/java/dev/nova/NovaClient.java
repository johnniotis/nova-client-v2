package dev.nova;

import com.mojang.blaze3d.platform.InputConstants;
import dev.nova.gui.ClickGuiScreen;
import dev.nova.module.ModuleManager;
import dev.nova.module.modules.EntityESP;
import dev.nova.module.modules.Freecam;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class NovaClient implements ClientModInitializer {
    public static final String MOD_ID = "novaclient";
    private static final ModuleManager MODULES = new ModuleManager();
    public static KeyMapping.Category CATEGORY;
    private static KeyMapping guiKey;

    public static ModuleManager modules() { return MODULES; }

    @Override
    public void onInitializeClient() {
        CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

        guiKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.novaclient.gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));

        // ---- Register modules here. One line per module. ----
        MODULES.register(new Freecam(GLFW.GLFW_KEY_F6));
        MODULES.register(new EntityESP(GLFW.GLFW_KEY_G));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MODULES.tick(client);
            while (guiKey.consumeClick()) {
                Minecraft.getInstance().setScreen(new ClickGuiScreen());
            }
        });
    }
}
