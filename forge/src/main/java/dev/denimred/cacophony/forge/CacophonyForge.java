package dev.denimred.cacophony.forge;

import dev.denimred.cacophony.Cacophony;
import dev.denimred.cacophony.CacophonyCommands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;

import static dev.denimred.cacophony.Cacophony.DISCORD;
import static dev.denimred.cacophony.Cacophony.MOD_ID;
import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

@Mod(MOD_ID)
public final class CacophonyForge {
    public CacophonyForge() {
        ModLoadingContext.get().registerExtensionPoint(DisplayTest.class, CacophonyForge::makeDisplayTest);
        Cacophony.init(() -> {
            EVENT_BUS.<ServerStartingEvent>addListener(e -> DISCORD.start(e.getServer()));
            EVENT_BUS.<ServerStoppedEvent>addListener(e -> DISCORD.stop());
            EVENT_BUS.<ServerChatEvent>addListener(e -> DISCORD.chat.receiveMC(e.getPlayer(), e.getMessage()));
            EVENT_BUS.<RegisterCommandsEvent>addListener(e -> CacophonyCommands.register(e.getDispatcher()));
        });
    }

    private static DisplayTest makeDisplayTest() {
        return new DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true);
    }
}
