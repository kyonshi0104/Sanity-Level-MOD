package com.kyonshi.command;

import com.kyonshi.SanityLevel;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class SanityCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sanity")
                .requires(source -> source.getEntity() instanceof Player)
                .then(Commands.literal("get")
                        .executes(SanityCommand::getSanity))
                .then(Commands.literal("set")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 20))
                                .executes(context -> setSanity(context, IntegerArgumentType.getInteger(context, "value")))))
                .then(Commands.literal("add")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(context -> addSanity(context, IntegerArgumentType.getInteger(context, "amount")))))
        );
    }

    private static int getSanity(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();

        if (player != null) {
            Integer value = player.getAttached(SanityLevel.SANITY);

            if (value == null) {
                value = 20;
                player.setAttached(SanityLevel.SANITY, value);
            }

            final int finalValue = value;
            source.sendSuccess(() -> Component.literal("現在の正気度: " + finalValue), false);
            return value;
        }
        return 0;
    }

    private static int setSanity(CommandContext<CommandSourceStack> context, int value) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();

        if (player != null) {
            com.kyonshi.util.SanityManager.setSanity(player, value);

            source.sendSuccess(() -> Component.literal("正気度を " + value + " に設定しました"), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("プレイヤーのみ実行可能です"));
            return 0;
        }
    }

    private static int addSanity(CommandContext<CommandSourceStack> context, int amount) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();

        if (player != null) {
            com.kyonshi.util.SanityManager.addSanity(player, amount);

            Integer next = player.getAttached(com.kyonshi.SanityLevel.SANITY);

            if (next == null) source.sendFailure(Component.literal("なんらかのエラーが発生しました。"));

            source.sendSuccess(() -> Component.literal("正気度を " + amount + " 変更しました (現在: " + next + ")"), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("プレイヤーのみ実行可能です"));
            return 0;
        }
    }
}