package com.kyonshi.client;

import com.kyonshi.SanityLevelClient;
import com.kyonshi.mixin.client.GameRendererAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.LoggerFactory;

public class SanityShaderHandler {
    private static final Identifier DESATURATE_WEAK = Identifier.fromNamespaceAndPath("sanity-level", "desaturate_weak");
    private static final Identifier BITS_MONOCHROME = Identifier.fromNamespaceAndPath("sanity-level", "desaturate");
    private static final Identifier BLUR_MONOCHROME = Identifier.fromNamespaceAndPath("sanity-level", "blur_desaturate");
    // BITS系
    private static final Identifier BITS_WEAK = Identifier.fromNamespaceAndPath("sanity-level", "bits_weak");
    private static final Identifier BITS_STRONG = Identifier.fromNamespaceAndPath("sanity-level", "bits_strong");

    // BLUR系 (追加)
    private static final Identifier BLUR_WEAK = Identifier.fromNamespaceAndPath("sanity-level", "blur_weak");
    private static final Identifier BLUR_STRONG = Identifier.fromNamespaceAndPath("sanity-level", "blur_strong");

    private static Identifier currentActiveShader = null;
    private static com.kyonshi.config.SanityConfig.ShaderType lastConfigType = null;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            com.kyonshi.config.SanityConfig.ShaderType currentType = com.kyonshi.config.SanityConfig.get().shaderType;

            Identifier actualShaderId = client.gameRenderer.currentPostEffect();

            Integer sanity = SanityLevelClient.currentSanity;
            if (sanity == null) return;

            Identifier targetShader = null;
            com.kyonshi.config.SanityConfig.ShaderType type = com.kyonshi.config.SanityConfig.get().shaderType;

            if (sanity <= 3) {
                targetShader = (currentType == com.kyonshi.config.SanityConfig.ShaderType.PIXELATED) ? BITS_STRONG : BLUR_STRONG;
            } else if (sanity <= 6) {
                targetShader = (currentType == com.kyonshi.config.SanityConfig.ShaderType.PIXELATED) ? BITS_MONOCHROME : BLUR_MONOCHROME;
            } else if (sanity <= 8) {
                targetShader = (currentType == com.kyonshi.config.SanityConfig.ShaderType.PIXELATED) ? BITS_WEAK : BLUR_WEAK;
            } else if (sanity <= 10) {
                targetShader = DESATURATE_WEAK;
            }

            if (actualShaderId == null || !actualShaderId.equals(targetShader)) {
                if (targetShader != null) {
                    ((GameRendererAccessor) client.gameRenderer).invokeSetPostEffect(targetShader);
                } else if (isModShader(actualShaderId)) {
                    client.gameRenderer.clearPostEffect();
                }
            }
        });
    }

    private static boolean isModShader(Identifier id) {
        if (id == null) return false;
        return id.getNamespace().equals("sanity-level");
    }
}