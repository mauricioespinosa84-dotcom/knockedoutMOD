package net.kaupenjoe.tutorialmod.mixin.client;

import net.kaupenjoe.tutorialmod.client.GrabClientState;
import net.kaupenjoe.tutorialmod.client.GrabbedClientState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {
    @Shadow public ModelPart body;
    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;
    @Shadow public ModelPart rightLeg;
    @Shadow public ModelPart leftLeg;

    // Extra offsets for 2nd/3rd person to match the first-person grab pose.
    private static final float TP_ROT_X = (float) Math.toRadians(-120.0F);
    private static final float TP_ROT_Y = (float) Math.toRadians(-35.0F);
    private static final float TP_ROT_Z = (float) Math.toRadians(0.0F);
    private static final float GRABBED_ARM_ROT_X = (float) Math.toRadians(-80.0F);
    private static final float GRABBED_ARM_ROT_Y = (float) Math.toRadians(35.0F);
    private static final float GRABBED_ARM_ROT_Z = (float) Math.toRadians(15.0F);

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void tutorialmod$applyGrabPose(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                           float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayer player)) {
            return;
        }

        if (GrabbedClientState.isGrabbed(player.getUUID())) {
            // Override the riding pose so the grabbed player looks upright while being carried.
            this.body.xRot = 0.0F;
            this.body.yRot = 0.0F;
            this.body.zRot = 0.0F;
            this.rightArm.xRot = GRABBED_ARM_ROT_X;
            this.rightArm.yRot = -GRABBED_ARM_ROT_Y;
            this.rightArm.zRot = GRABBED_ARM_ROT_Z;
            this.leftArm.xRot = GRABBED_ARM_ROT_X;
            this.leftArm.yRot = GRABBED_ARM_ROT_Y;
            this.leftArm.zRot = -GRABBED_ARM_ROT_Z;
            this.rightLeg.xRot = 0.0F;
            this.rightLeg.yRot = 0.0F;
            this.rightLeg.zRot = 0.0F;
            this.leftLeg.xRot = 0.0F;
            this.leftLeg.yRot = 0.0F;
            this.leftLeg.zRot = 0.0F;

            Object self = this;
            if (self instanceof PlayerModel<?> playerModel) {
                PlayerModelAccessor accessor = (PlayerModelAccessor) playerModel;
                ModelPart rightSleeve = accessor.tutorialmod$getRightSleeve();
                rightSleeve.xRot = GRABBED_ARM_ROT_X;
                rightSleeve.yRot = -GRABBED_ARM_ROT_Y;
                rightSleeve.zRot = GRABBED_ARM_ROT_Z;

                ModelPart leftSleeve = accessor.tutorialmod$getLeftSleeve();
                leftSleeve.xRot = GRABBED_ARM_ROT_X;
                leftSleeve.yRot = GRABBED_ARM_ROT_Y;
                leftSleeve.zRot = -GRABBED_ARM_ROT_Z;
            }

            return;
        }

        if (!GrabClientState.isGrabbing(player.getUUID())) {
            return;
        }

        // No necesitamos llamar a GrabArmPoseHelper si queremos una pose fija de "estrangulamiento"
        // O si lo usas, asegúrate de que devuelva 0,0,0.

        // Asignamos directamente los valores calculados para el agarre
        this.rightArm.xRot = TP_ROT_X; // Brazo levantado al cuello
        this.rightArm.yRot = TP_ROT_Y; // Brazo cerrado al centro
        this.rightArm.zRot = TP_ROT_Z; // Sin inclinación

        // Aplicamos lo mismo a la manga (sleeve)
        Object self = this;
        if (self instanceof PlayerModel<?> playerModel) {
            ModelPart sleeve = ((PlayerModelAccessor) playerModel).tutorialmod$getRightSleeve();
            sleeve.xRot = TP_ROT_X;
            sleeve.yRot = TP_ROT_Y;
            sleeve.zRot = TP_ROT_Z;
        }
    }

}
