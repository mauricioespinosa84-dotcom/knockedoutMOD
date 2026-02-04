package net.kaupenjoe.tutorialmod.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityPassengerMixin {
    private static final double HOLD_DISTANCE = 0.8D;
    private static final double HOLD_EYE_OFFSET = -1.35D;

    @Inject(method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", at = @At("HEAD"), cancellable = true)
    private void tutorialmod$positionGrabbedRider(Entity passenger, Entity.MoveFunction moveFunction, CallbackInfo ci) {
        if (!((Object) this instanceof Player grabber)) {
            return;
        }

        if (!(passenger instanceof Player)) {
            return;
        }

        if (passenger.getVehicle() != (Object) this) {
            return;
        }

        Vec3 eye = grabber.getEyePosition(1.0F);
        Vec3 forward = grabber.getLookAngle();
        Vec3 holdPos = eye.add(forward.scale(HOLD_DISTANCE)).add(0.0D, HOLD_EYE_OFFSET, 0.0D);
        moveFunction.accept(passenger, holdPos.x, holdPos.y, holdPos.z);
        ci.cancel();
    }
}
