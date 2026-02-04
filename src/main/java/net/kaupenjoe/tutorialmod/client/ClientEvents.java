package net.kaupenjoe.tutorialmod.client;

import com.mojang.math.Axis;
import net.kaupenjoe.tutorialmod.TutorialMod;
import net.kaupenjoe.tutorialmod.client.grab.GrabArmPoseHelper;
import net.kaupenjoe.tutorialmod.client.KnockdownClientState;
import net.kaupenjoe.tutorialmod.client.GrabbedClientState;
import net.kaupenjoe.tutorialmod.client.grab.GrabArmPoseHelper.GrabArmPose;
import net.kaupenjoe.tutorialmod.network.ModMessages;
import net.kaupenjoe.tutorialmod.network.packet.C2SGrabActionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientEvents {
    // First-person offsets (tweak to taste)
    private static final double FP_ARM_OFFSET_X = 0.55D;
    private static final double FP_ARM_OFFSET_Y = -0.35D;
    private static final double FP_ARM_OFFSET_Z = -0.9D;
    private static final float FP_ARM_ROT_X = -40.0F;
    private static final float FP_ARM_ROT_Y = 20.0F;
    private static final float FP_ARM_ROT_Z = 10.0F;

    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        if (ModKeyBindings.GRAB.consumeClick()) {
            ModMessages.sendToServer(new C2SGrabActionPacket());
        }

        KnockdownClientState.clientTick(minecraft);
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        GrabClientState.clear();
        GrabbedClientState.clear();
        GrabArmPoseHelper.clear();
        KnockdownClientState.clear();
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        if (!GrabClientState.isGrabbing(player.getUUID())) {
            return;
        }

        GrabArmPose pose = GrabArmPoseHelper.getPose(player, event.getPartialTick());
        if (pose == null) {
            return;
        }

        event.getPoseStack().translate(FP_ARM_OFFSET_X, FP_ARM_OFFSET_Y, FP_ARM_OFFSET_Z);
        event.getPoseStack().mulPose(Axis.XP.rotation(pose.xRot() + (float) Math.toRadians(FP_ARM_ROT_X)));
        event.getPoseStack().mulPose(Axis.YP.rotation(pose.yRot() + (float) Math.toRadians(FP_ARM_ROT_Y)));
        event.getPoseStack().mulPose(Axis.ZP.rotation(pose.zRot() + (float) Math.toRadians(FP_ARM_ROT_Z)));
    }
}
