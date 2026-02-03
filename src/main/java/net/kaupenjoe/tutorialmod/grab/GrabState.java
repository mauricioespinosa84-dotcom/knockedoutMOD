package net.kaupenjoe.tutorialmod.grab;

import net.kaupenjoe.tutorialmod.TutorialMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class GrabState {
    private static final String ROOT = TutorialMod.MOD_ID + ".grab_state";
    private static final String KEY_GRABBED_BY = "grabbedBy";
    private static final String KEY_GRABBING = "grabbing";
    private static final String KEY_KNOCKDOWN_TICKS = "knockdownTicks";

    private GrabState() {
    }

    public static UUID getGrabbedBy(Player player) {
        CompoundTag tag = getTag(player);
        return tag.hasUUID(KEY_GRABBED_BY) ? tag.getUUID(KEY_GRABBED_BY) : null;
    }

    public static void setGrabbedBy(Player player, UUID grabberId) {
        CompoundTag tag = getTag(player);
        if (grabberId == null) {
            tag.remove(KEY_GRABBED_BY);
        } else {
            tag.putUUID(KEY_GRABBED_BY, grabberId);
        }
    }

    public static UUID getGrabbing(Player player) {
        CompoundTag tag = getTag(player);
        return tag.hasUUID(KEY_GRABBING) ? tag.getUUID(KEY_GRABBING) : null;
    }

    public static void setGrabbing(Player player, UUID targetId) {
        CompoundTag tag = getTag(player);
        if (targetId == null) {
            tag.remove(KEY_GRABBING);
        } else {
            tag.putUUID(KEY_GRABBING, targetId);
        }
    }

    public static int getKnockdownTicks(Player player) {
        return getTag(player).getInt(KEY_KNOCKDOWN_TICKS);
    }

    public static void setKnockdownTicks(Player player, int ticks) {
        CompoundTag tag = getTag(player);
        tag.putInt(KEY_KNOCKDOWN_TICKS, Math.max(0, ticks));
    }

    public static void clearAll(Player player) {
        player.getPersistentData().remove(ROOT);
    }

    private static CompoundTag getTag(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(ROOT, Tag.TAG_COMPOUND)) {
            data.put(ROOT, new CompoundTag());
        }
        return data.getCompound(ROOT);
    }
}
