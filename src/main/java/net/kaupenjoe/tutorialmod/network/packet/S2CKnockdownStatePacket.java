package net.kaupenjoe.tutorialmod.network.packet;

import net.kaupenjoe.tutorialmod.client.KnockdownClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CKnockdownStatePacket {
    private final UUID playerId;
    private final int ticks;

    public S2CKnockdownStatePacket(UUID playerId, int ticks) {
        this.playerId = playerId;
        this.ticks = ticks;
    }

    public S2CKnockdownStatePacket(FriendlyByteBuf buf) {
        this.playerId = buf.readUUID();
        this.ticks = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
        buf.writeInt(ticks);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> KnockdownClientState.setTicks(playerId, ticks));
        context.setPacketHandled(true);
        return true;
    }
}
