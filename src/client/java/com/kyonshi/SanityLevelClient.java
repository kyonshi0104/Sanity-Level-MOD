package com.kyonshi;

import com.kyonshi.client.SanityHudOverlay;
import com.kyonshi.client.SanityShaderHandler;
import net.fabricmc.api.ClientModInitializer;
import com.kyonshi.network.SanitySyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.LoggerFactory;

public class SanityLevelClient implements ClientModInitializer {
    public static int currentSanity = 20;

    @Override
    public void onInitializeClient() {
        // パケット受信時の処理
        ClientPlayNetworking.registerGlobalReceiver(SanitySyncPayload.ID, (payload, context) -> {
            LoggerFactory.getLogger("sanity-level").info(String.valueOf("currentSanity"));
            context.client().execute(() -> {
                currentSanity = payload.sanity();
                LoggerFactory.getLogger("sanity-level").info(String.valueOf(currentSanity));
            });
        });

        SanityHudOverlay.register();
        SanityShaderHandler.register();
    }
}