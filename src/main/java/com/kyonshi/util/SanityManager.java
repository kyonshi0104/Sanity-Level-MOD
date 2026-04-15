package com.kyonshi.util;

import com.kyonshi.SanityLevel;
import com.kyonshi.network.SanitySyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class SanityManager {

    // --- 既存のメソッド ---

    public static void syncSanity(ServerPlayer player, int value) {
        ServerPlayNetworking.send(player, new SanitySyncPayload(value));
    }

    public static void setSanity(Player player, int value) {
        int clampedValue = Mth.clamp(value, 0, 20);
        int currentValue = player.getAttachedOrElse(SanityLevel.SANITY, 20);

        if (currentValue != clampedValue) {
            player.setAttached(SanityLevel.SANITY, clampedValue);

            if (player instanceof ServerPlayer serverPlayer) {
                syncSanity(serverPlayer, clampedValue);
            }
        }
    }

    public static void addSanity(Player player, int amount) {
        setSanity(player, player.getAttachedOrElse(SanityLevel.SANITY, 20) + amount);
    }

    public static int getSanity(Player player) {
        return player.getAttachedOrElse(SanityLevel.SANITY, 20);
    }


    public static boolean isSanityCritical(ServerPlayer player) {
        return getSanity(player) <= 4;
    }

    public static void dropCurrentItem(ServerPlayer player) {
        InteractionHand hand = InteractionHand.MAIN_HAND;
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.isEmpty()) {
            hand = InteractionHand.OFF_HAND;
            itemStack = player.getItemInHand(hand);
        }

        if (!itemStack.isEmpty()) {
            ItemStack droppedStack = itemStack.copy();

            if (player.isUsingItem()) {
                player.stopUsingItem();
            }

            player.setItemInHand(hand, ItemStack.EMPTY);
            player.drop(droppedStack, false, true);

            player.swing(hand, true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.PLAYERS, 0.4f, 0.5f);
        }
    }

    public static void forceDropActiveItem(ServerPlayer player) {

        ItemStack itemStack = player.getActiveItem();
        InteractionHand activeHand = player.getUsedItemHand();

        if (!itemStack.isEmpty()) {
            ItemStack droppedStack = itemStack.copy();

            player.stopUsingItem();
            player.setItemInHand(activeHand, ItemStack.EMPTY);

            player.drop(droppedStack, false, true);

            player.swing(activeHand, true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.PLAYERS, 0.4f, 0.5f);
        }
    }
}