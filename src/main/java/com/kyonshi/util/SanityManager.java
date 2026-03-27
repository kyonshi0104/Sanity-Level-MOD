package com.kyonshi.util;

import com.kyonshi.SanityLevel;
import com.kyonshi.network.SanitySyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class SanityManager {

    public static void syncSanity(ServerPlayer player, int value) {
        // S2C パケット送信
        ServerPlayNetworking.send(player, new SanitySyncPayload(value));
    }

    public static void setSanity(Player player, int value) {
        int clampedValue = Mth.clamp(value, 0, 20);
        player.setAttached(SanityLevel.SANITY, clampedValue);

        if (player instanceof ServerPlayer serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new SanitySyncPayload(clampedValue));
        }
    }

    public static void addSanity(Player player, int amount) {
        setSanity(player, player.getAttachedOrElse(SanityLevel.SANITY,20) + amount);
    }
}