package net.kaupenjoe.tutorialmod.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class GrabbedClientState {
    private static final Set<UUID> GRABBED = new HashSet<>();

    private GrabbedClientState() {
    }

    public static void setGrabbed(UUID playerId, boolean grabbed) {
        if (grabbed) {
            GRABBED.add(playerId);
        } else {
            GRABBED.remove(playerId);
        }
    }

    public static boolean isGrabbed(UUID playerId) {
        return GRABBED.contains(playerId);
    }

    public static void clear() {
        GRABBED.clear();
    }
}
