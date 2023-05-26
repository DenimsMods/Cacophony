package dev.denimred.cacophony.fabric;

import dev.denimred.cacophony.Cacophony;
import dev.denimred.cacophony.CacophonyCommands;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import static dev.denimred.cacophony.Cacophony.DISCORD;

public final class CacophonyFabric implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Cacophony.init(() -> {
            // Discord start/stop
            ServerLifecycleEvents.SERVER_STARTING.register(DISCORD::start);
            ServerLifecycleEvents.SERVER_STOPPED.register(s -> DISCORD.stop());
            // Chat syncing
            CacophonyEvents.CHAT.register(DISCORD.chat::receiveMC);
            // Presence updating based on player join/leave
            CacophonyEvents.PLAYER_ADDED.register(p -> DISCORD.bot.updatePresence());
            CacophonyEvents.PLAYER_REMOVED.register(p -> DISCORD.bot.updatePresence());
            // Command registration
            CommandRegistrationCallback.EVENT.register((dispatcher, b) -> CacophonyCommands.register(dispatcher));
        });
    }
}
