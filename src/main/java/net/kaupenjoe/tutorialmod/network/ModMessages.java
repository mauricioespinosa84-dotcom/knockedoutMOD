package net.kaupenjoe.tutorialmod.network;

import net.kaupenjoe.tutorialmod.TutorialMod;
import net.kaupenjoe.tutorialmod.network.packet.C2SGrabActionPacket;
import net.kaupenjoe.tutorialmod.network.packet.S2CGrabStatePacket;
import net.kaupenjoe.tutorialmod.network.packet.S2CKnockdownStatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


public final class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TutorialMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int packetId = 0;

    private ModMessages() {
    }

    public static void register() {
        CHANNEL.messageBuilder(C2SGrabActionPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SGrabActionPacket::new)
                .encoder(C2SGrabActionPacket::toBytes)
                .consumerMainThread(C2SGrabActionPacket::handle)
                .add();

        CHANNEL.messageBuilder(S2CGrabStatePacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CGrabStatePacket::new)
                .encoder(S2CGrabStatePacket::toBytes)
                .consumerMainThread(S2CGrabStatePacket::handle)
                .add();

        CHANNEL.messageBuilder(S2CKnockdownStatePacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CKnockdownStatePacket::new)
                .encoder(S2CKnockdownStatePacket::toBytes)
                .consumerMainThread(S2CKnockdownStatePacket::handle)
                .add();
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }

    public static void sendToTrackingAndSelf(Object message, Entity entity) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }
}
