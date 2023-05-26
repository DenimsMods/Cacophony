package dev.denimred.cacophony.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.level.ServerPlayer;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

public final class CacophonyEvents {
    private CacophonyEvents() {}

    public static final Event<ChatEvent> CHAT = ChatEvent.create();

    @FunctionalInterface
    public interface ChatEvent {
        void onChat(ServerPlayer speaker, String msg);

        private static Event<ChatEvent> create() {
            return createArrayBacked(ChatEvent.class, events -> (speaker, msg) -> {
                for (ChatEvent event : events) event.onChat(speaker, msg);
            });
        }
    }

    public static final Event<PlayerAddedEvent> PLAYER_ADDED = PlayerAddedEvent.create();

    @FunctionalInterface
    public interface PlayerAddedEvent {
        void onPlayerAdded(ServerPlayer player);

        private static Event<PlayerAddedEvent> create() {
            return createArrayBacked(PlayerAddedEvent.class, events -> player -> {
                for (PlayerAddedEvent event : events) event.onPlayerAdded(player);
            });
        }
    }

    public static final Event<PlayerRemovedEvent> PLAYER_REMOVED = PlayerRemovedEvent.create();

    @FunctionalInterface
    public interface PlayerRemovedEvent {
        void onPlayerRemoved(ServerPlayer player);

        private static Event<PlayerRemovedEvent> create() {
            return createArrayBacked(PlayerRemovedEvent.class, events -> player -> {
                for (PlayerRemovedEvent event : events) event.onPlayerRemoved(player);
            });
        }
    }
}
