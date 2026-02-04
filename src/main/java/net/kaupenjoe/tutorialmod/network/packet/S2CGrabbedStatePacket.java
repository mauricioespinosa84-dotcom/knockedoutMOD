package net.kaupenjoe.tutorialmod.network.packet;

import net.kaupenjoe.tutorialmod.client.GrabbedClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CGrabbedStatePacket {
    private final UUID playerId;
    private final boolean grabbed;

    public S2CGrabbedStatePacket(UUID playerId, boolean grabbed) {
        this.playerId = playerId;
        this.grabbed = grabbed;
    }

    public S2CGrabbedStatePacket(FriendlyByteBuf buf) {
        this.playerId = buf.readUUID();
        this.grabbed = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
        buf.writeBoolean(grabbed);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> GrabbedClientState.setGrabbed(playerId, grabbed));
        context.setPacketHandled(true);
        return true;
    }
}
