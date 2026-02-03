package net.kaupenjoe.tutorialmod.network.packet;

import net.kaupenjoe.tutorialmod.grab.GrabManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SGrabActionPacket {
    public C2SGrabActionPacket() {
    }

    public C2SGrabActionPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                GrabManager.handleGrabAction(player);
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}
