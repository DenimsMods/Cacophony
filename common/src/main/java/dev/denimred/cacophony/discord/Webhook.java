package dev.denimred.cacophony.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import dev.denimred.cacophony.config.CacophonyConfig;

import static dev.denimred.cacophony.Cacophony.CONFIG;

public class Webhook {
    private JDAWebhookClient client;

    public boolean isUsable() {
        return client != null && isEnabled();
    }

    public boolean isEnabled() {
        return CONFIG.getWebhook().isEnabled();
    }

    public void init() {
        if (client != null || !isEnabled()) return;

        String webhookUrl = CONFIG.getWebhook().getUrl();
        if (webhookUrl == null) {
            CacophonyConfig.complain("Webhook is enabled but the url is missing! Webhook will be disabled");
            return;
        }

        if (!WebhookClientBuilder.WEBHOOK_PATTERN.matcher(webhookUrl).matches()) {
            CacophonyConfig.complain("Webhook url is invalid! Webhook will be disabled");
            return;
        }

        client = JDAWebhookClient.withUrl(webhookUrl);
    }

    public void stop() {
        if (client == null) return;
        client.close();
        client = null;
    }

    public void restart() {
        stop();
        init();
    }

    public void send(WebhookMessage message) {
        if (isUsable()) client.send(message);
    }
}
