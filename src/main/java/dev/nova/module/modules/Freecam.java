package dev.nova.module.modules;

import com.mojang.authlib.GameProfile;
import dev.nova.NovaClient;
import dev.nova.module.Module;
import dev.nova.setting.NumSetting;
import net.minecraft.client.Options;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Detaches the camera from your player. The mouse turns the CAMERA (your real player keeps
 * facing the same way), WASD flies along where you look, Space/Shift go up/down, Sprint = boost.
 */
public class Freecam extends Module {
    public final NumSetting speed = num("Speed", 1.0, 0.1, 5.0, 0.1);

    private RemotePlayer cam;
    private ClientInput savedInput;
    private float savedYaw, savedPitch;
    private Vec3 vel = Vec3.ZERO;

    public Freecam(int key) {
        super("Freecam", "Fly the camera around; mouse turns the camera.", key);
    }

    /** Called from EntityMixin when the player's mouse look changes. Returns true if we consumed it. */
    public static boolean handleTurn(Entity entity, double dYaw, double dPitch) {
        Freecam f = NovaClient.modules().get(Freecam.class);
        if (f == null || !f.isEnabled() || f.cam == null || entity != f.mc.player) return false;
        f.rotate(dYaw, dPitch);
        return true;
    }

    private void rotate(double dYaw, double dPitch) {
        float yaw = cam.getYRot() + (float) dYaw * 0.15f;
        float pitch = Mth.clamp(cam.getXRot() + (float) dPitch * 0.15f, -90f, 90f);
        cam.setYRot(yaw);
        cam.setXRot(pitch);
        cam.yRotO = yaw;   // no interpolation lag on turning
        cam.xRotO = pitch;
    }

    @Override
    protected void onEnable() {
        if (mc.player == null || mc.level == null) { setEnabled(false); return; }
        cam = new RemotePlayer(mc.level, new GameProfile(UUID.randomUUID(), "Freecam"));
        cam.setPos(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        cam.setYRot(mc.player.getYRot());
        cam.setXRot(mc.player.getXRot());
        cam.yRotO = cam.getYRot();
        cam.xRotO = cam.getXRot();
        syncPrev();
        savedYaw = mc.player.getYRot();
        savedPitch = mc.player.getXRot();
        vel = Vec3.ZERO;
        savedInput = mc.player.input;
        mc.player.input = new ClientInput();   // blank input so the real player doesn't walk
        mc.setCameraEntity(cam);
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            if (savedInput != null) mc.player.input = savedInput;
            mc.setCameraEntity(mc.player);
        }
        cam = null;
        savedInput = null;
    }

    private void syncPrev() {
        cam.xo = cam.getX(); cam.yo = cam.getY(); cam.zo = cam.getZ();
    }

    @Override
    public void onTick() {
        if (cam == null || mc.player == null) return;

        // Fallback: if the mixin didn't catch the mouse, move the turn from the player to the camera here.
        float dy = mc.player.getYRot() - savedYaw;
        float dp = mc.player.getXRot() - savedPitch;
        if (Math.abs(dy) > 0.001f || Math.abs(dp) > 0.001f) {
            rotate(dy / 0.15, dp / 0.15);
            mc.player.setYRot(savedYaw);
            mc.player.setXRot(savedPitch);
            mc.player.yRotO = savedYaw;
            mc.player.xRotO = savedPitch;
        }

        Options o = mc.options;
        double yr = Math.toRadians(cam.getYRot());
        double pr = Math.toRadians(cam.getXRot());
        Vec3 look = new Vec3(-Math.sin(yr) * Math.cos(pr), -Math.sin(pr), Math.cos(yr) * Math.cos(pr));
        Vec3 right = new Vec3(-Math.cos(yr), 0, -Math.sin(yr));

        double f = (o.keyUp.isDown() ? 1 : 0) - (o.keyDown.isDown() ? 1 : 0);
        double s = (o.keyRight.isDown() ? 1 : 0) - (o.keyLeft.isDown() ? 1 : 0);
        double v = (o.keyJump.isDown() ? 1 : 0) - (o.keyShift.isDown() ? 1 : 0);

        Vec3 target = look.scale(f).add(right.scale(s)).add(0, v, 0);
        if (target.lengthSqr() > 0) {
            double sp = speed.get() * (o.keySprint.isDown() ? 3.0 : 1.0);
            target = target.normalize().scale(sp);
        }
        vel = vel.add(target.subtract(vel).scale(0.4)); // smooth acceleration

        syncPrev(); // keep last tick's position so rendering interpolates smoothly
        cam.setPos(cam.getX() + vel.x, cam.getY() + vel.y, cam.getZ() + vel.z);
    }
}
