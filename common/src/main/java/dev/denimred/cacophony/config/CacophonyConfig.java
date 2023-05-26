package dev.denimred.cacophony.config;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.denimred.cacophony.Cacophony.LOGGER;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "BooleanMethodIsAlwaysInverted"}) // spicy

public final class CacophonyConfig extends SingletonJsonConfig<CacophonyConfig> {
    public static final String CONFIG_WIKI_URL = "https://github.com/DenimsMods/Cacophony/wiki/Configuration";

    public static void complain(String msg) {
        LOGGER.error(msg);
        LOGGER.error("You can learn how to configure Cacophony here: " + CONFIG_WIKI_URL);
    }

    public CacophonyConfig() {
        super("cacophony.json");
    }

    /** Bot configuration. */
    private BotConfig bot = new BotConfig();
    /** Webhook configuration. */
    private WebhookConfig webhook = new WebhookConfig();

    public @NotNull BotConfig getBot() {
        if (bot == null) {
            bot = new BotConfig();
            save();
        }
        return bot;
    }

    public @NotNull WebhookConfig getWebhook() {
        if (webhook == null) {
            webhook = new WebhookConfig();
            save();
        }
        return webhook;
    }

    public static class BotConfig {
        /** If true, the bot system will be enabled. */
        private boolean enabled = false;
        /** The bot's token. Required if enabled. */
        private String token = null;
        /** The channel the bot uses to sync chat. Required if enabled. */
        private Long channel = null;
        /** The message format used by the bot. */
        private String messageFormat = "<%1$s> %2$s";
        /** The presence for the bot to use when running. */
        private PresenceConfig presence = new PresenceConfig();

        public boolean isEnabled() {
            return enabled;
        }

        public @Nullable String getToken() {
            if (token != null) return token;
            String prop = System.getProperty("cacophony.bot.token");
            return prop != null ? prop : System.getenv("CACOPHONY_BOT_TOKEN");
        }

        public @Nullable Long getChannel() {
            return channel;
        }

        public @NotNull String formatMessage(String speakerName, String msg) {
            return messageFormat.formatted(speakerName, msg).strip();
        }

        public @Nullable PresenceConfig getPresence() {
            return presence;
        }

        public static class PresenceConfig {
            /** The online status for the bot to use when running. */
            @JsonAdapter(OnlineStatusAdapter.class)
            private OnlineStatus status = OnlineStatus.ONLINE;
            /** The activity for the bot to use when running. */
            private ActivityConfig activity = new ActivityConfig();

            public @NotNull OnlineStatus getStatus() {
                if (status == null) status = OnlineStatus.ONLINE;
                return status;
            }

            public @Nullable ActivityConfig getActivity() {
                return activity;
            }

            public static class ActivityConfig {
                /** The activity type. */
                private Type type = Type.PLAYING;
                /** The activity name. Will display after the type (e.g. "Playing with 3 players") */
                private String name = "with %d players";
                /** The stream url to be used by the streaming activity type. */
                private String url = null;

                public @Nullable ActivityType getType() {
                    return type != null ? type.jdaType : null;
                }

                public @Nullable String getName() {
                    return name;
                }

                public @Nullable String getUrl() {
                    return url;
                }

                public enum Type {
                    @SerializedName("playing") PLAYING(ActivityType.PLAYING),
                    @SerializedName("streaming") STREAMING(ActivityType.STREAMING),
                    @SerializedName("listening") LISTENING(ActivityType.LISTENING),
                    @SerializedName("competing") COMPETING(ActivityType.COMPETING);

                    public final ActivityType jdaType;

                    Type(ActivityType jdaType) {
                        this.jdaType = jdaType;
                    }
                }
            }
        }
    }

    public static class WebhookConfig {
        /** If true, the webhook system will be enabled. */
        private boolean enabled = false;
        /** The url of the webhook. Required if enabled. */
        private String url = null;
        /** The webhook style; plain posts regular messages while fancy overrides the username/avatar of the webhook. */
        private Style style = Style.FANCY;
        /** The message format used by the plain style. */
        private String plainMessageFormat = "<%1$s> %2$s";
        /** The webhook username override used by the plain style. */
        private String plainUsername = null;
        /** The webhook avatar override url used by the plain style. */
        private String plainAvatarUrl = null;
        /** The avatar provider service to source the avatars for the fancy style. */
        private String fancyAvatarProvider = "https://crafatar.com/renders/head/%1$s?size=128&overlay";

        public boolean isEnabled() {
            return enabled;
        }

        public @Nullable String getUrl() {
            if (url != null) return url;
            String prop = System.getProperty("cacophony.webhook.url");
            return prop != null ? prop : System.getenv("CACOPHONY_WEBHOOK_URL");
        }

        public @NotNull Style getStyle() {
            if (style == null) style = Style.FANCY;
            return style;
        }

        public @Nullable String getPlainUsername() {
            return plainUsername != null ? plainUsername.strip() : null;
        }

        public @Nullable String getPlainAvatarUrl() {
            return plainAvatarUrl != null ? plainAvatarUrl.strip() : null;
        }

        public @NotNull String formatPlainMessage(String speakerName, String msg) {
            return plainMessageFormat.formatted(speakerName, msg).strip();
        }

        public @Nullable String formatFancyAvatar(ServerPlayer speaker) {
            String speakerUUID = speaker.getStringUUID().replace("-", "");
            return fancyAvatarProvider != null ? fancyAvatarProvider.formatted(speakerUUID).strip() : null;
        }

        public enum Style {
            @SerializedName("plain") PLAIN, @SerializedName("fancy") FANCY
        }
    }
}
