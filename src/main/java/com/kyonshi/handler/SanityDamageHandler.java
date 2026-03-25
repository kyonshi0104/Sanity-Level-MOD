package com.kyonshi.handler;

import com.kyonshi.util.SanityManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class SanityDamageHandler {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, amount, HALTED_AMOUNT, fatal) -> {
            if (entity instanceof ServerPlayer player) {
                processDamageReduction(player, source, amount);
            }
        });
    }

    private static void processDamageReduction(ServerPlayer player, DamageSource source, float amount) {
        // 1. 全ての4以上のダメージ
        if (amount >= 4.0f) {
            SanityManager.addSanity(player, -2);
        }

    }
}