package net.kaupenjoe.tutorialmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class KnockdownClientState {
    private static final Map<UUID, Integer> KNOCKED_DOWN = new HashMap<>();

    private KnockdownClientState() {
    }

    public static void setTicks(UUID playerId, int ticks) {
        if (ticks <= 0) {
            KNOCKED_DOWN.remove(playerId);
        } else {
            KNOCKED_DOWN.put(playerId, ticks);
        }
    }

    public static void clear() {
        KNOCKED_DOWN.clear();
    }

    public static void clientTick(Minecraft minecraft) {
        if (KNOCKED_DOWN.isEmpty() || minecraft.level == null) {
            return;
        }

        Iterator<Map.Entry<UUID, Integer>> iterator = KNOCKED_DOWN.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            UUID playerId = entry.getKey();
            int ticks = entry.getValue();

            Player player = minecraft.level.getPlayerByUUID(playerId);
            if (player != null) {
                player.setForcedPose(Pose.SWIMMING);
            }

            int nextTicks = ticks - 1;
            if (nextTicks <= 0) {
                iterator.remove();
                if (player != null) {
                    player.setForcedPose(null);
                }
            } else {
                entry.setValue(nextTicks);
            }
        }
    }
}
