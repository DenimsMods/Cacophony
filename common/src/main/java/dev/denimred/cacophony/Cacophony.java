package dev.denimred.cacophony;

import dev.denimred.cacophony.config.CacophonyConfig;
import dev.denimred.cacophony.discord.Discord;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Cacophony {
    private Cacophony() {}

    public static final String MOD_ID = "cacophony";
    public static final Logger LOGGER = LogManager.getLogger("Cacophony");
    public static final CacophonyPlatform PLATFORM = CacophonyPlatform.getInstance();
    public static final CacophonyConfig CONFIG = new CacophonyConfig();
    public static final Discord DISCORD = new Discord();

    public static void init(Runnable onSuccess) {
        if (PLATFORM.isOnServer()) {
            CONFIG.load();
            DISCORD.init();
            onSuccess.run();
        } else LOGGER.info("Cacophony is a server-only mod; it will do nothing on the client");
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
