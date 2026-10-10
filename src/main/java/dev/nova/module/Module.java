package dev.nova.module;

import dev.nova.setting.BoolSetting;
import dev.nova.setting.NumSetting;
import dev.nova.setting.Setting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public final String description;
    public final List<Setting> settings = new ArrayList<>();
    protected final Minecraft mc = Minecraft.getInstance();
    KeyMapping keyMapping; // set by ModuleManager
    private boolean enabled;
    private final int defaultKey;

    protected Module(String name, String description, int defaultKey) {
        this.name = name;
        this.description = description;
        this.defaultKey = defaultKey;
    }

    protected BoolSetting bool(String name, boolean def) {
        BoolSetting s = new BoolSetting(name, def);
        settings.add(s);
        return s;
    }

    protected NumSetting num(String name, double def, double min, double max, double step) {
        NumSetting s = new NumSetting(name, def, min, max, step);
        settings.add(s);
        return s;
    }

    int defaultKey() { return defaultKey; }
    public boolean isEnabled() { return enabled; }
    public void toggle() { setEnabled(!enabled); }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
    }

    public String keyName() {
        return keyMapping == null ? "" : keyMapping.getTranslatedKeyMessage().getString();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    /** Called every client tick while enabled and in a world. */
    public void onTick() {}
}
