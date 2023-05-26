package dev.denimred.cacophony;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

import static dev.denimred.cacophony.Cacophony.CONFIG;
import static dev.denimred.cacophony.Cacophony.DISCORD;
import static net.minecraft.commands.Commands.literal;

public final class CacophonyCommands {
    private CacophonyCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var root = literal(Cacophony.MOD_ID).requires(css -> css.hasPermission(Commands.LEVEL_ADMINS));

        root.then(reload());

        dispatcher.register(root);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reload() {
        var reload = literal("reload");

        var all = literal("all").executes(ctx -> {
            success(ctx, "Reloading everything");
            CONFIG.load();
            DISCORD.bot.restart();
            DISCORD.webhook.restart();
            return 3;
        });

        var config = literal("config").executes(ctx -> {
            success(ctx, "Reloading config");
            CONFIG.load();
            DISCORD.bot.updatePresence();
            return 1;
        });

        var bot = literal("bot").executes(ctx -> {
            success(ctx, "Reloading bot");
            DISCORD.bot.restart();
            return 1;
        });

        var webhook = literal("webhook").executes(ctx -> {
            success(ctx, "Reloading webhook");
            DISCORD.webhook.restart();
            return 1;
        });

        return reload.then(all).then(config).then(bot).then(webhook);
    }

    private static void success(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendSuccess(new TextComponent("Cacophony: " + msg), true);
    }
}
