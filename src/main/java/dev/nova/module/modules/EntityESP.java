package dev.nova.module.modules;

import dev.nova.module.Module;
import dev.nova.setting.BoolSetting;
import dev.nova.setting.NumSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Outlines entities through walls with the vanilla glow outline. Colors: players red,
 * hostile mobs orange, other mobs green, items cyan.
 */
public class EntityESP extends Module {
    public final BoolSetting players = bool("Players", true);
    public final BoolSetting hostile = bool("Hostile mobs", true);
    public final BoolSetting passive = bool("Passive mobs", false);
    public final BoolSetting items = bool("Items", false);
    public final NumSetting range = num("Range", 128, 16, 256, 8);

    private final Set<Entity> glowing = Collections.newSetFromMap(new WeakHashMap<>());

    public EntityESP(int key) {
        super("ESP", "Outline entities through walls.", key);
    }

    private boolean shouldHighlight(Entity e) {
        if (e == mc.player || e.isRemoved()) return false;
        if (e instanceof Player) return players.get();
        if (e instanceof Enemy) return hostile.get();
        if (e instanceof Mob) return passive.get();
        if (e instanceof ItemEntity) return items.get();
        return false;
    }

    /** Outline color for this entity, or null to leave vanilla behaviour. Used by EntityMixin. */
    public Integer colorFor(Entity e) {
        if (!isEnabled() || !glowing.contains(e)) return null;
        if (e instanceof Player) return 0xFF3B3B;
        if (e instanceof Enemy) return 0xFF9800;
        if (e instanceof Mob) return 0x4CAF50;
        if (e instanceof ItemEntity) return 0x00BCD4;
        return null;
    }

    @Override
    public void onTick() {
        if (mc.level == null || mc.player == null) return;
        double r = range.get();
        double r2 = r * r;
        for (Entity e : mc.level.entitiesForRendering()) {
            boolean want = shouldHighlight(e) && mc.player.distanceToSqr(e) <= r2;
            if (want) {
                e.setGlowingTag(true); // re-applied every tick: the server may reset the flag
                glowing.add(e);
            } else if (glowing.remove(e)) {
                e.setGlowingTag(false);
            }
        }
    }

    @Override
    protected void onDisable() {
        for (Entity e : new ArrayList<>(glowing)) e.setGlowingTag(false);
        glowing.clear();
    }
}
