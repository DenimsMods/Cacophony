package dev.denimred.cacophony.discord;

import dev.denimred.cacophony.config.CacophonyConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.denimred.cacophony.Cacophony.CONFIG;
import static dev.denimred.cacophony.Cacophony.LOGGER;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;

public final class Bot {
    private final Discord discord;
    public JDA jda;

    public Bot(Discord discord) {
        this.discord = discord;
    }

    public boolean isUsable() {
        return jda != null && isEnabled();
    }

    public boolean isEnabled() {
        return CONFIG.getBot().isEnabled();
    }

    public void init() {
        if (jda != null || !isEnabled()) return;

        String token = CONFIG.getBot().getToken();
        if (token == null) {
            CacophonyConfig.complain("Bot is enabled but the token is missing! Bot will be disabled");
            return;
        }

        if (CONFIG.getBot().getChannel() == null) {
            CacophonyConfig.complain("Bot is enabled but the channel is undefined! Bot will be disabled");
            return;
        }

        jda = JDABuilder.createLight(token, GUILD_MESSAGES, MESSAGE_CONTENT)
                        .setIdle(true)
                        .setStatus(OnlineStatus.IDLE)
                        .build();

        jda.addEventListener((EventListener) event -> {
            if (event instanceof MessageReceivedEvent e) discord.chat.receiveDC(e.getAuthor(), e.getMessage());
            else if (event instanceof ReadyEvent) updatePresence();
        });
    }

    public void start() {
        if (isUsable()) try {
            jda.awaitReady();
        } catch (Exception e) {
            LOGGER.error("Failed to start JDA", e);
        }
    }

    public void stop() {
        if (jda == null) return;
        jda.shutdownNow();
        jda = null;
    }

    public void restart() {
        stop();
        init();
    }

    public void updatePresence() {
        if (!isUsable()) return;
        jda.getPresence().setPresence(getStatus(), getActivity(), false);
    }

    private @NotNull OnlineStatus getStatus() {
        var presenceCfg = CONFIG.getBot().getPresence();
        return presenceCfg != null ? presenceCfg.getStatus() : OnlineStatus.ONLINE;
    }

    private @Nullable Activity getActivity() {
        var presenceCfg = CONFIG.getBot().getPresence();
        if (presenceCfg == null) return null;

        var activityCfg = presenceCfg.getActivity();
        if (activityCfg == null) return null;

        String rawName = activityCfg.getName();
        ActivityType type = activityCfg.getType();
        if (rawName == null || type == null) return null;

        MinecraftServer server = discord.chat.getGameServer();
        int players = server != null ? server.getPlayerCount() : 0;
        String formatted = rawName.formatted(players).strip();
        String name = formatted.length() > 128 ? formatted.substring(0, 128) : formatted;
        if (name.isBlank()) return null;

        return Activity.of(type, name, activityCfg.getUrl());
    }
}
