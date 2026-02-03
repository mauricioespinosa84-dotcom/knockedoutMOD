package net.kaupenjoe.tutorialmod.network.packet;

import net.kaupenjoe.tutorialmod.client.GrabClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CGrabStatePacket {
    private final UUID playerId;
    private final boolean grabbing;

    public S2CGrabStatePacket(UUID playerId, boolean grabbing) {
        this.playerId = playerId;
        this.grabbing = grabbing;
    }

    public S2CGrabStatePacket(FriendlyByteBuf buf) {
        this.playerId = buf.readUUID();
        this.grabbing = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
        buf.writeBoolean(grabbing);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> GrabClientState.setGrabbing(playerId, grabbing));
        context.setPacketHandled(true);
        return true;
    }
}
