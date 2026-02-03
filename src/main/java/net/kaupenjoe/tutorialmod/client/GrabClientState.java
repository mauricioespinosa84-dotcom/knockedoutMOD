package net.kaupenjoe.tutorialmod.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class GrabClientState {
    private static final Set<UUID> GRABBERS = new HashSet<>();

    private GrabClientState() {
    }

    public static void setGrabbing(UUID playerId, boolean grabbing) {
        if (grabbing) {
            GRABBERS.add(playerId);
        } else {
            GRABBERS.remove(playerId);
        }
    }

    public static boolean isGrabbing(UUID playerId) {
        return GRABBERS.contains(playerId);
    }

    public static void clear() {
        GRABBERS.clear();
    }
}
