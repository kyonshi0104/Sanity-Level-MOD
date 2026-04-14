package com.kyonshi;

import com.kyonshi.client.SanityHudOverlay;
import com.kyonshi.client.SanityShaderHandler;
import com.kyonshi.config.SanityConfig;
import net.fabricmc.api.ClientModInitializer;
import com.kyonshi.network.SanitySyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class SanityLevelClient implements ClientModInitializer {
    public static int currentSanity = 20;

    @Override
    public void onInitializeClient() {
        SanityConfig.register();
        // パケット受信時の処理
        ClientPlayNetworking.registerGlobalReceiver(SanitySyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                currentSanity = payload.sanity();
                SanityShaderHandler.updateShader(currentSanity);
            });
        });

        SanityHudOverlay.register();
        SanityShaderHandler.register();
    }
}