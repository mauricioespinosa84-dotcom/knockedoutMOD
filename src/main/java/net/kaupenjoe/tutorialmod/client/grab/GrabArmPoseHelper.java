package net.kaupenjoe.tutorialmod.client.grab;

import net.minecraft.client.player.AbstractClientPlayer;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GrabArmPoseHelper {
    private static final GrabArmModel MODEL = new GrabArmModel();
    private static final Map<UUID, GrabArmAnimatable> ANIMATABLES = new HashMap<>();

    private GrabArmPoseHelper() {
    }

    public static GrabArmPose getPose(AbstractClientPlayer player, float partialTick) {
        GrabArmAnimatable animatable = ANIMATABLES.computeIfAbsent(player.getUUID(), GrabArmAnimatable::new);
        long instanceId = animatable.getOwnerId().getMostSignificantBits() ^ animatable.getOwnerId().getLeastSignificantBits();

        AnimationState<GrabArmAnimatable> state = new AnimationState<>(animatable, 0, 0, partialTick, false);
        state.setData(DataTickets.TICK, player.tickCount + (double) partialTick);

        MODEL.getBakedModel(MODEL.getModelResource(animatable));
        MODEL.handleAnimations(animatable, instanceId, state);

        GeoBone bone = MODEL.getBone("right_arm").orElse(null);
        if (bone == null) {
            return null;
        }

        return new GrabArmPose(bone.getRotX(), bone.getRotY(), bone.getRotZ());
    }

    public static void clear() {
        ANIMATABLES.clear();
    }

    public record GrabArmPose(float xRot, float yRot, float zRot) {
    }
}
