package dev.nova.mixin;

import dev.nova.NovaClient;
import dev.nova.module.modules.EntityESP;
import dev.nova.module.modules.Freecam;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Both injections use require = 0, so if a method name changes the game still starts
// (Freecam turning then falls back to the tick-based method, ESP just uses vanilla outline colors).
@Mixin(Entity.class)
public abstract class EntityMixin {

    /** Mouse look: send it to the freecam camera instead of the real player. */
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true, require = 0)
    private void nova$turn(double yRot, double xRot, CallbackInfo ci) {
        if (Freecam.handleTurn((Entity) (Object) this, yRot, xRot)) ci.cancel();
    }

    /** Glow outline color for ESP. */
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true, require = 0)
    private void nova$teamColor(CallbackInfoReturnable<Integer> cir) {
        EntityESP esp = NovaClient.modules().get(EntityESP.class);
        if (esp == null) return;
        Integer c = esp.colorFor((Entity) (Object) this);
        if (c != null) cir.setReturnValue(c);
    }
}
