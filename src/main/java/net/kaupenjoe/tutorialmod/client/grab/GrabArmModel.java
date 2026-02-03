package net.kaupenjoe.tutorialmod.client.grab;

import net.kaupenjoe.tutorialmod.TutorialMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GrabArmModel extends GeoModel<GrabArmAnimatable> {
    private static final ResourceLocation MODEL = new ResourceLocation(TutorialMod.MOD_ID, "geo/grab_arm.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation(TutorialMod.MOD_ID, "animations/grab_arm.animation.json");
    private static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("minecraft", "textures/entity/steve.png");

    @Override
    public ResourceLocation getModelResource(GrabArmAnimatable animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GrabArmAnimatable animatable) {
        ResourceLocation texture = animatable.getTexture();
        return texture != null ? texture : FALLBACK_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GrabArmAnimatable animatable) {
        return ANIMATION;
    }
}
