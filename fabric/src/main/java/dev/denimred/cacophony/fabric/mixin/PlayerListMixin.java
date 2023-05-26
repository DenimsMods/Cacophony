package dev.denimred.cacophony.fabric.mixin;

import dev.denimred.cacophony.fabric.CacophonyEvents;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(at = @At("TAIL"), method = "placeNewPlayer")
    private void onHandleChat(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        CacophonyEvents.PLAYER_ADDED.invoker().onPlayerAdded(player);
    }

    @Inject(at = @At("TAIL"), method = "remove")
    private void onHandleChat(ServerPlayer player, CallbackInfo ci) {
        CacophonyEvents.PLAYER_REMOVED.invoker().onPlayerRemoved(player);
    }
}
