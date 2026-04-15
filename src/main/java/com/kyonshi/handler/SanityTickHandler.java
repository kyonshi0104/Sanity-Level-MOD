package com.kyonshi.handler;

import com.kyonshi.SanityLevel;
import com.kyonshi.util.SanityManager;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SanityTickHandler {
    private static final ResourceKey<DamageType> FRENZY_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(SanityLevel.MOD_ID, "frenzy"));
    private static final Logger S_LOGGER = LoggerFactory.getLogger("sanity-level");;


    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long tickCount = server.getTickCount();
            if (tickCount % 20 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    processContinuousLogic(player, tickCount);
                }
            }

        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            SanityManager.syncSanity(newPlayer, 20);
        });

        EntitySleepEvents.STOP_SLEEPING.register((entity, pos) -> {
            if (entity instanceof ServerPlayer player) {
                // プレイヤーが十分に長く寝たか」を判定
                if (player.isSleepingLongEnough()) {
                    int current = player.getAttachedOrElse(SanityLevel.SANITY,20);

                    if (current < 15) {
                        SanityManager.setSanity(player, 15);
                    } else {
                        SanityManager.addSanity(player, 3);
                    }
                }
            }
        });
    }

    private static void processContinuousLogic(ServerPlayer player, long tickCount) {

        if (player.hasEffect(SanityLevel.CALM)) {
            return;
        }

        float change = 0.0f;

        ServerLevel level = player.level();
        BlockPos eyePos = BlockPos.containing(player.getEyePosition());
        BlockPos footPos = player.blockPosition();

        // --- 耐性チェック ---
        boolean hasNightVision = player.hasEffect(MobEffects.NIGHT_VISION);
        boolean hasTamedNearby = checkTamedNearby(player, level, footPos);
        boolean immuneToEnv = hasNightVision || hasTamedNearby;



        // 暗闇
        if (!hasNightVision) {
            if (level.getLightEngine().getRawBrightness(eyePos, 0) <= 2) change -= 1.0f;
        }

        // 環境・バイオーム
        var biome = level.getBiome(footPos);
        if (!immuneToEnv) {
            if (biome.is(Biomes.DEEP_DARK) || biome.is(Biomes.PALE_GARDEN)) {
                change -= 0.2f;
            }
            if (level.dimension() == Level.NETHER || level.dimension() == Level.END) {
                change -= 0.2f;
            }
        }
        if (footPos.getY() < 0) change -= 0.05f;

        // Mob接近
        if (!hasTamedNearby) {
            change -= calculateMobThreat(player, level, footPos);
        }

        // ステータス異常
        if (player.hasEffect(MobEffects.WITHER) || player.hasEffect(MobEffects.POISON) ||
                player.hasEffect(MobEffects.INFESTED) || player.hasEffect(MobEffects.HUNGER)) {
            change -= 0.5f;
        }


        // 日光
        boolean isDaytime = level.getDayTime() % 24000 < 12000;
        if (isDaytime && !level.isThundering() && !level.isRaining() && level.canSeeSky(footPos)) {
            change += 0.05f;
        }

        //回復バイオーム
        if (biome.is(Biomes.FLOWER_FOREST) || biome.is(Biomes.CHERRY_GROVE)) {
            change += 0.1f;
        }

        // 焚き火 (半径15ブロック)
        if (isNearCampfire(level, footPos)) {
            change += 0.2f;
        }

        // テイム済みモブの存在 (明るい場所 & 12ブロック以内)
        if (hasTamedNearby && level.getLightEngine().getRawBrightness(footPos, 0) > 7) {
            change += 0.05f;
        }

        // 最終計算
        if (Math.abs(change) >= 1.0f) {
            SanityManager.addSanity(player, (int) (change > 0 ? Math.floor(change) : Math.ceil(change)));
        } else if (change != 0 && Math.random() < Math.abs(change)) {
            SanityManager.addSanity(player, (change > 0 ? 1 : -1));
        }

        handleAnomalies(player, player.getAttachedOrElse(SanityLevel.SANITY,20), level.getGameTime());
    }


    private static void handleAnomalies(ServerPlayer player, int sanity, long tickCount) {

        if (sanity <= 12) {
            player.causeFoodExhaustion(0.05f);
        }

        // 幻聴
        if (sanity <= 8 && player.getRandom().nextFloat() < 0.05f) {
            playHallucination(player);
        }

        // 空腹の加速
        if (sanity <= 6) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false, false));
        }


        if (sanity <= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 0, false, false, false));
        }


        if (sanity <= 0) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1, false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false, false));

            player.hurt(player.damageSources().source(FRENZY_KEY), 1.0f);
        }

    }

    private static void playHallucination(ServerPlayer player) {
        var sounds = List.of(
                SoundEvents.CREEPER_PRIMED,
                SoundEvents.ZOMBIE_AMBIENT,
                SoundEvents.ENDERMAN_AMBIENT,
                SoundEvents.ARROW_SHOOT
        );
        var sound = sounds.get(player.getRandom().nextInt(sounds.size()));

        Vec3 lookVec = player.getLookAngle().reverse().normalize().scale(2.5);
        Vec3 soundPos = player.position().add(lookVec);
        player.level().playSound(
                null,
                soundPos.x, soundPos.y, soundPos.z,
                sound,
                SoundSource.AMBIENT,
                0.5f,
                1.0f
        );
    }

    private static float calculateMobThreat(ServerPlayer player, ServerLevel level, BlockPos pos) {
        float threat = 0.0f;
        List<Entity> nearbyEntities = level.getEntities(player, new AABB(pos).inflate(12.0));
        boolean hasWarden = false;
        boolean hasMidThreat = false;
        boolean hasLowThreat = false;

        for (Entity e : nearbyEntities) {
            if (e.getType() == EntityType.WARDEN) hasWarden = true;
            else if (e instanceof Creeper || e instanceof EnderMan || e.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
                hasMidThreat = true;
            } else if (e instanceof Monster) {
                hasLowThreat = true;
            }
        }
        if (hasWarden) threat += 1.0f;
        else if (hasMidThreat) threat += 0.2f;
        else if (hasLowThreat) threat += 0.1f;

        return threat;
    }

    private static boolean checkTamedNearby(ServerPlayer player, ServerLevel level, BlockPos pos) {
        return !level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(12.0),
                e -> {
                    if (e instanceof OwnableEntity ownable) {
                        var reference = ownable.getOwnerReference();
                        return reference != null && player.getUUID().equals(reference.getUUID());
                    }
                    return false;
                }).isEmpty();
    }

    private static boolean isNearCampfire(ServerLevel level, BlockPos pos) {
        BlockPos corner1 = pos.offset(-15, -1, -15);
        BlockPos corner2 = pos.offset(15, 1, 15);

        return BlockPos.betweenClosedStream(corner1, corner2)
                .filter(p -> {
                    var state = level.getBlockState(p);
                    return state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE);
                })
                .anyMatch(p -> {
                    return level.getBlockState(p).getValue(CampfireBlock.LIT);
                });
    }

}