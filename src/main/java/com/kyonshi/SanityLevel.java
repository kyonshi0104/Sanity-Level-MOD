package com.kyonshi;

import com.kyonshi.command.SanityCommand;
import com.kyonshi.handler.SanityDamageHandler;
import com.kyonshi.handler.SanityDeathWitnessHandler;
import com.kyonshi.handler.SanityTickHandler;
import com.kyonshi.network.SanitySyncPayload;
import com.kyonshi.util.SanityManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import org.slf4j.LoggerFactory;

public class SanityLevel implements ModInitializer {
    public static final String MOD_ID = "sanity-level";

    public static final Holder<MobEffect> CALM =
            Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(MOD_ID, "calm"), new calmEffect());

    public static final Holder<MobEffect> SANITY_RECOVERY =
            Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(MOD_ID, "sanity_recovery"), new SanityRecoveryEffect());


    public static final AttachmentType<Integer> SANITY = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .initializer(() -> 20)
            .buildAndRegister(Identifier.fromNamespaceAndPath(MOD_ID, "sanity"));

    @Override
    public void onInitialize() {

        // コマンド登録
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SanityCommand.register(dispatcher);
        });

        PayloadTypeRegistry.playS2C().register(SanitySyncPayload.ID, SanitySyncPayload.CODEC);

        SanityTickHandler.register();
        SanityDamageHandler.register();
        SanityDeathWitnessHandler.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();

            if (!player.hasAttached(SanityLevel.SANITY)) {
                player.setAttached(SanityLevel.SANITY, 20);
            }

            int currentSanity = player.getAttached(SanityLevel.SANITY);

            SanityManager.syncSanity(player, currentSanity);

        });

        ModItems.initialize();
        ModPotions.initialize();
    }
}