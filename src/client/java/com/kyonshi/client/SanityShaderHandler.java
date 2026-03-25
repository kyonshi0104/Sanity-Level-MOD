package com.kyonshi.client;

import com.kyonshi.SanityLevel;
import com.kyonshi.SanityLevelClient;
import com.kyonshi.mixin.client.GameRendererAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.LoggerFactory;

public class SanityShaderHandler {
    private static final Identifier DESATURATE_WEAK = Identifier.fromNamespaceAndPath("sanity-level", "desaturate_weak");
    private static final Identifier BITS_WEAK = Identifier.fromNamespaceAndPath("sanity-level", "bits_weak");
    private static final Identifier MONOCHROME = Identifier.fromNamespaceAndPath("sanity-level", "desaturate");
    private static final Identifier BITS_STRONG = Identifier.fromNamespaceAndPath("sanity-level", "bits_strong");

    private static Identifier currentActiveShader = null;

    

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.player == null || client.gameRenderer == null) {
                if (currentActiveShader != null) {
                    client.gameRenderer.clearPostEffect();
                    currentActiveShader = null;
                }
                return;
            }

            if (client.player.hasEffect(SanityLevel.CALM)) {
                if (currentActiveShader != null) {
                    client.gameRenderer.clearPostEffect();
                    currentActiveShader = null;
                }
                return;
            }

            Integer sanity = SanityLevelClient.currentSanity;
            if (sanity == null) return;

            Identifier targetShader = null;

            if (sanity <= 3) {
                targetShader = BITS_STRONG;
            } else if (sanity <= 6) {
                targetShader = MONOCHROME;
            } else if (sanity <= 8) {
                targetShader = BITS_WEAK;
            } else if (sanity <= 10) {
                targetShader = DESATURATE_WEAK;
            }

            if (currentActiveShader != targetShader) {
                if (targetShader != null) {
                    ((GameRendererAccessor) client.gameRenderer).invokeSetPostEffect(targetShader);
                    LoggerFactory.getLogger("sanity-level").info("Shader Applied: " + targetShader.getPath());
                } else {
                    client.gameRenderer.clearPostEffect();
                    LoggerFactory.getLogger("sanity-level").info("Shader Cleared");
                }
                currentActiveShader = targetShader;
            }
        });
    }
}