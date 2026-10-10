package dev.nova.module;

import com.mojang.blaze3d.platform.InputConstants;
import dev.nova.NovaClient;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public void register(Module module) {
        module.keyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.novaclient." + module.name.toLowerCase(),
                InputConstants.Type.KEYSYM, module.defaultKey(), NovaClient.CATEGORY));
        modules.add(module);
    }

    public List<Module> all() { return Collections.unmodifiableList(modules); }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> type) {
        for (Module m : modules) if (type.isInstance(m)) return (T) m;
        return null;
    }

    public void tick(Minecraft client) {
        boolean inWorld = client.player != null && client.level != null;
        for (Module m : modules) {
            while (m.keyMapping.consumeClick()) {
                if (inWorld) m.toggle();
            }
            if (!inWorld) {
                if (m.isEnabled()) m.setEnabled(false); // left the world: shut down cleanly
            } else if (m.isEnabled()) {
                m.onTick();
            }
        }
    }
}
