package net.kaupenjoe.tutorialmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class ModKeyBindings {
    public static final String CATEGORY = "key.categories.tutorialmod";
    public static final KeyMapping GRAB = new KeyMapping(
            "key.tutorialmod.grab",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            CATEGORY
    );

    private ModKeyBindings() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(GRAB);
    }
}
