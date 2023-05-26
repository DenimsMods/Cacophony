package dev.denimred.cacophony.fabric.mixin;

import dev.denimred.cacophony.fabric.CacophonyEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastMessage(Lnet/minecraft/network/chat/Component;Ljava/util/function/Function;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"), method = "handleChat(Lnet/minecraft/server/network/TextFilter$FilteredText;)V")
    private void onHandleChat(TextFilter.FilteredText filteredText, CallbackInfo ci) {
        CacophonyEvents.CHAT.invoker().onChat(player, filteredText.getRaw());
    }
}
