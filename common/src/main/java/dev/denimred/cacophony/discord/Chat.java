package dev.denimred.cacophony.discord;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import static dev.denimred.cacophony.Cacophony.CONFIG;
import static net.minecraft.Util.NIL_UUID;
import static net.minecraft.network.chat.ChatType.CHAT;

public final class Chat {
    private final Discord discord;
    private MinecraftServer gameServer;

    public Chat(Discord discord) {
        this.discord = discord;
    }

    public MinecraftServer getGameServer() {
        return gameServer;
    }

    public void setGameServer(MinecraftServer gameServer) {
        this.gameServer = gameServer;
    }

    public void receiveDC(User speaker, Message raw) {
        if (speaker.isBot() || speaker.isSystem()) return;
        if (gameServer == null) return;
        String formatted = "<%s> %s".formatted(speaker.getName(), raw.getContentStripped());
        TextComponent msg = new TextComponent(formatted);
        gameServer.executeIfPossible(() -> gameServer.getPlayerList().broadcastMessage(msg, CHAT, NIL_UUID));
    }

    public void receiveMC(ServerPlayer speaker, String raw) {
        String speakerName = speaker.getScoreboardName().strip();
        if (discord.webhook.isUsable()) {
            WebhookMessageBuilder msg = new WebhookMessageBuilder();
            var cfg = CONFIG.getWebhook();
            switch (cfg.getStyle()) {
                case PLAIN -> msg.setContent(cfg.formatPlainMessage(speakerName, raw))
                                 .setUsername(cfg.getPlainUsername())
                                 .setAvatarUrl(cfg.getPlainAvatarUrl());
                case FANCY -> msg.setContent(raw.strip())
                                 .setUsername(speakerName)
                                 .setAvatarUrl(cfg.formatFancyAvatar(speaker));
            }
            discord.webhook.send(msg.build());
        } else if (discord.bot.isUsable()) {
            var cfg = CONFIG.getBot();
            Long channelId = cfg.getChannel();
            if (channelId == null) return;
            TextChannel channel = discord.bot.jda.getTextChannelById(channelId);
            if (channel == null) return;
            channel.sendMessage(cfg.formatMessage(speakerName, raw)).submit();
        }
    }
}
