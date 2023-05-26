package dev.denimred.cacophony.discord;

import dev.denimred.cacophony.config.CacophonyConfig;
import net.minecraft.server.MinecraftServer;

import static dev.denimred.cacophony.Cacophony.LOGGER;

public final class Discord {
    public final Chat chat = new Chat(this);
    public final Bot bot = new Bot(this);
    public final Webhook webhook = new Webhook();

    public void init() {
        try {
            if (!bot.isEnabled() && !webhook.isEnabled()) {
                CacophonyConfig.complain("Neither the bot nor webhook are enabled! Cacophony will be non-functional");
            } else {
                bot.init();
                webhook.init();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Discord handler", e);
        }
    }

    public void start(MinecraftServer server) {
        chat.setGameServer(server);
        bot.start();
    }

    public void stop() {
        bot.stop();
        webhook.stop();
        chat.setGameServer(null);
    }
}
