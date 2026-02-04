package net.kaupenjoe.tutorialmod.grab;

import net.kaupenjoe.tutorialmod.network.ModMessages;
import net.kaupenjoe.tutorialmod.network.packet.S2CGrabStatePacket;
import net.kaupenjoe.tutorialmod.network.packet.S2CKnockdownStatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.projectile.ProjectileUtil;

import java.util.UUID;

public final class GrabManager {
    private static final double GRAB_RANGE = 3.5D;
    private static final double HOLD_DISTANCE = 0.8D;
    private static final double HOLD_EYE_OFFSET = -1.35D;
    private static final float HELD_YAW_OFFSET = 180.0F;
    private static final int KNOCKDOWN_TICKS = 120;
    private static final double THROW_STRENGTH = 1.2D;
    private static final double THROW_UP = 0.4D;
    private static final float LOW_HEALTH_FRACTION = 0.30F;

    private GrabManager() {
    }

    public static void handleGrabAction(ServerPlayer grabber) {
        if (grabber.isSpectator()) {
            return;
        }

        if (GrabState.getKnockdownTicks(grabber) > 0) {
            return;
        }

        if (GrabState.getGrabbedBy(grabber) != null) {
            return;
        }

        UUID currentTargetId = GrabState.getGrabbing(grabber);
        if (currentTargetId != null) {
            throwTarget(grabber, currentTargetId);
            return;
        }

        ServerPlayer target = findTarget(grabber);
        if (target == null || target.isSpectator()) {
            return;
        }

        if (GrabState.getGrabbedBy(target) != null || GrabState.getKnockdownTicks(target) > 0) {
            return;
        }

        if (!isLowHealth(target)) {
            return;
        }

        startGrab(grabber, target);
    }

    public static void serverTick(ServerPlayer player) {
        if (GrabState.getGrabbedBy(player) != null) {
            tickGrabbed(player);
            return;
        }

        tickKnockdown(player);

        UUID targetId = GrabState.getGrabbing(player);
        if (targetId != null) {
            ServerPlayer target = player.server.getPlayerList().getPlayer(targetId);
            UUID grabbedBy = target != null ? GrabState.getGrabbedBy(target) : null;
            if (target == null || grabbedBy == null || !player.getUUID().equals(grabbedBy) || target.level() != player.level()) {
                GrabState.setGrabbing(player, null);
                ModMessages.sendToTrackingAndSelf(new S2CGrabStatePacket(player.getUUID(), false), player);
            }
        }
    }

    public static void forceRelease(ServerPlayer player) {
        UUID targetId = GrabState.getGrabbing(player);
        if (targetId != null) {
            ServerPlayer target = player.server.getPlayerList().getPlayer(targetId);
            if (target != null) {
                GrabState.setGrabbedBy(target, null);
                target.setNoGravity(false);
            }
            GrabState.setGrabbing(player, null);
            ModMessages.sendToTrackingAndSelf(new S2CGrabStatePacket(player.getUUID(), false), player);
        }

        UUID grabberId = GrabState.getGrabbedBy(player);
        if (grabberId != null) {
            ServerPlayer grabber = player.server.getPlayerList().getPlayer(grabberId);
            if (grabber != null) {
                GrabState.setGrabbing(grabber, null);
                ModMessages.sendToTrackingAndSelf(new S2CGrabStatePacket(grabber.getUUID(), false), grabber);
            }
            GrabState.setGrabbedBy(player, null);
            player.setNoGravity(false);
        }

        GrabState.setKnockdownTicks(player, 0);
        ModMessages.sendToTrackingAndSelf(new S2CKnockdownStatePacket(player.getUUID(), 0), player);
        if (player.getForcedPose() == Pose.SWIMMING) {
            player.setForcedPose(null);
        }
    }

    private static void startGrab(ServerPlayer grabber, ServerPlayer target) {
        GrabState.setGrabbing(grabber, target.getUUID());
        GrabState.setGrabbedBy(target, grabber.getUUID());

        target.setDeltaMovement(Vec3.ZERO);
        target.setNoGravity(true);

        ModMessages.sendToTrackingAndSelf(new S2CGrabStatePacket(grabber.getUUID(), true), grabber);
    }

    private static void throwTarget(ServerPlayer grabber, UUID targetId) {
        ServerPlayer target = grabber.server.getPlayerList().getPlayer(targetId);
        releaseGrab(grabber, target);

        if (target == null || !isLowHealth(target)) {
            return;
        }

        Vec3 look = grabber.getLookAngle();
        Vec3 velocity = new Vec3(look.x * THROW_STRENGTH, look.y * THROW_STRENGTH + THROW_UP, look.z * THROW_STRENGTH);
        target.setDeltaMovement(velocity);
        target.hurtMarked = true;

        GrabState.setKnockdownTicks(target, KNOCKDOWN_TICKS);
        ModMessages.sendToTrackingAndSelf(new S2CKnockdownStatePacket(target.getUUID(), KNOCKDOWN_TICKS), target);
    }

    private static void tickGrabbed(ServerPlayer grabbed) {
        UUID grabberId = GrabState.getGrabbedBy(grabbed);
        if (grabberId == null) {
            return;
        }

        ServerPlayer grabber = grabbed.server.getPlayerList().getPlayer(grabberId);
        if (grabber == null || !grabber.isAlive() || grabber.level() != grabbed.level()) {
            releaseGrab(grabber, grabbed);
            return;
        }

        if (!isLowHealth(grabbed)) {
            releaseGrab(grabber, grabbed);
            return;
        }

        Vec3 holdPos = getHoldPosition(grabber);
        float yaw = grabber.getYRot() + HELD_YAW_OFFSET;
        grabbed.connection.teleport(holdPos.x, holdPos.y, holdPos.z, yaw, 0.0F);
        grabbed.setDeltaMovement(Vec3.ZERO);
        grabbed.setNoGravity(true);
        grabbed.fallDistance = 0.0F;
        grabbed.setYRot(yaw);
        grabbed.setXRot(0.0F);
    }

    private static void tickKnockdown(ServerPlayer player) {
        int ticks = GrabState.getKnockdownTicks(player);
        if (ticks <= 0) {
            if (player.getForcedPose() == Pose.SWIMMING) {
                player.setForcedPose(null);
            }
            return;
        }

        int nextTicks = ticks - 1;
        GrabState.setKnockdownTicks(player, nextTicks);
        player.setForcedPose(Pose.SWIMMING);
        player.setDeltaMovement(Vec3.ZERO);
        player.setSprinting(false);

        if (nextTicks <= 0) {
            player.setForcedPose(null);
            ModMessages.sendToTrackingAndSelf(new S2CKnockdownStatePacket(player.getUUID(), 0), player);
        }
    }

    private static Vec3 getHoldPosition(ServerPlayer grabber) {
        Vec3 eye = grabber.getEyePosition(1.0F);
        Vec3 forward = grabber.getLookAngle();
        return eye.add(forward.scale(HOLD_DISTANCE)).add(0.0D, HOLD_EYE_OFFSET, 0.0D);
    }

    private static boolean isLowHealth(ServerPlayer player) {
        return player.getHealth() <= player.getMaxHealth() * LOW_HEALTH_FRACTION;
    }

    private static void releaseGrab(ServerPlayer grabber, ServerPlayer grabbed) {
        if (grabber != null) {
            GrabState.setGrabbing(grabber, null);
            ModMessages.sendToTrackingAndSelf(new S2CGrabStatePacket(grabber.getUUID(), false), grabber);
        }
        if (grabbed != null) {
            GrabState.setGrabbedBy(grabbed, null);
            grabbed.setNoGravity(false);
        }
    }

    private static ServerPlayer findTarget(ServerPlayer grabber) {
        Vec3 start = grabber.getEyePosition(1.0F);
        Vec3 look = grabber.getLookAngle();
        Vec3 end = start.add(look.scale(GRAB_RANGE));

        HitResult blockHit = grabber.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, grabber));
        if (blockHit.getType() != HitResult.Type.MISS) {
            end = blockHit.getLocation();
        }

        AABB box = grabber.getBoundingBox().expandTowards(look.scale(GRAB_RANGE)).inflate(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(grabber.level(), grabber, start, end, box,
                entity -> entity instanceof ServerPlayer && entity != grabber);

        if (entityHit == null) {
            return null;
        }

        return (ServerPlayer) entityHit.getEntity();
    }
}
