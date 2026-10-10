package dev.nova.gui;

import dev.nova.NovaClient;
import dev.nova.module.Module;
import dev.nova.setting.BoolSetting;
import dev.nova.setting.NumSetting;
import dev.nova.setting.Setting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** Menu: click a module name to toggle it, click a setting to change it (click along a slider to set it). */
public class ClickGuiScreen extends Screen {
    private static final int X = 20, Y = 20, W = 200, H = 15;

    private record Row(Module module, Setting setting, int y) {}

    public ClickGuiScreen() { super(Component.literal("Nova Client")); }

    private List<Row> rows() {
        List<Row> rows = new ArrayList<>();
        int y = Y + H;
        for (Module m : NovaClient.modules().all()) {
            rows.add(new Row(m, null, y));
            y += H;
            for (Setting s : m.settings) {
                rows.add(new Row(m, s, y));
                y += H;
            }
            y += 4;
        }
        return rows;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        g.fill(X, Y, X + W, Y + H, 0xFF111111);
        g.text(this.font, "Nova Client", X + 5, Y + 4, 0xFFFFFFFF);

        int lastY = Y + H;
        for (Row r : rows()) {
            int y = r.y();
            lastY = y + H;
            boolean hover = mouseX >= X && mouseX <= X + W && mouseY >= y && mouseY < y + H;

            if (r.setting() == null) {
                Module m = r.module();
                int bg = m.isEnabled() ? (hover ? 0xFF3FA044 : 0xFF2E7D32) : (hover ? 0xFF444444 : 0xFF2A2A2A);
                g.fill(X, y, X + W, y + H, bg);
                g.text(this.font, m.name, X + 5, y + 4, 0xFFFFFFFF);
                String right = "[" + m.keyName() + "]  " + (m.isEnabled() ? "ON" : "OFF");
                g.text(this.font, right, X + W - 5 - this.font.width(right), y + 4,
                        m.isEnabled() ? 0xFFCCFFCC : 0xFFAAAAAA);
            } else if (r.setting() instanceof BoolSetting b) {
                g.fill(X, y, X + W, y + H, hover ? 0xFF3A3A3A : 0xFF1E1E1E);
                g.text(this.font, b.name, X + 12, y + 4, 0xFFDDDDDD);
                String v = b.get() ? "ON" : "OFF";
                g.text(this.font, v, X + W - 5 - this.font.width(v), y + 4, b.get() ? 0xFF66FF66 : 0xFFFF6666);
            } else if (r.setting() instanceof NumSetting n) {
                g.fill(X, y, X + W, y + H, 0xFF1E1E1E);
                int fillW = (int) ((W - 16) * n.fraction());
                g.fill(X + 8, y + 1, X + 8 + fillW, y + H - 1, hover ? 0xFF3F7FBF : 0xFF2F5F8F);
                String t = n.name + ": " + (n.step >= 1 ? String.valueOf((int) n.get()) : String.format("%.1f", n.get()));
                g.text(this.font, t, X + 12, y + 4, 0xFFFFFFFF);
            }
        }
        g.text(this.font, "Click a module to toggle, click settings to change. Esc to close.",
                X, lastY + 6, 0xFF999999);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            for (Row r : rows()) {
                int y = r.y();
                if (event.x() >= X && event.x() <= X + W && event.y() >= y && event.y() < y + H) {
                    if (r.setting() == null) {
                        if (this.minecraft != null && this.minecraft.player != null) r.module().toggle();
                    } else if (r.setting() instanceof BoolSetting b) {
                        b.toggle();
                    } else if (r.setting() instanceof NumSetting n) {
                        n.setFraction((event.x() - (X + 8)) / (double) (W - 16));
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
