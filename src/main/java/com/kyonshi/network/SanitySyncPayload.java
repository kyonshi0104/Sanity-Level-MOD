package com.kyonshi.network;

import com.kyonshi.SanityLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;


public record SanitySyncPayload(int sanity) implements CustomPacketPayload {
    public static final Type<SanitySyncPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(SanityLevel.MOD_ID, "sanity_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SanitySyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SanitySyncPayload::sanity,
            SanitySyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}