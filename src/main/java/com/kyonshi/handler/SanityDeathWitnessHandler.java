package com.kyonshi.handler;

import com.kyonshi.util.SanityManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SanityDeathWitnessHandler {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            boolean isVillager = entity instanceof Villager;
            boolean isTamed = (entity instanceof OwnableEntity ownable && ownable.getOwnerReference().getUUID() != null);

            if (isVillager || isTamed) {
                double radius = 16.0;
                AABB area = entity.getBoundingBox().inflate(radius);
                List<ServerPlayer> nearbyPlayers = entity.level().getEntitiesOfClass(ServerPlayer.class, area);

                for (ServerPlayer player : nearbyPlayers) {
                    // 壁越し確認
                    if (player.hasLineOfSight(entity)) {
                        if (isTamed) {
                            SanityManager.addSanity(player, -10);
                        } else {
                            SanityManager.addSanity(player, -2);
                        }
                    }
                }
            }
        });
    }
}