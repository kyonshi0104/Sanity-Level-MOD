package com.kyonshi.client;

import com.kyonshi.SanityLevelClient;
import com.kyonshi.mixin.client.GameRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

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

    private static Identifier lastAppliedShader = null;

    public static void register() {
        // Initial shader update is deferred until renderer is available.
        // currentSanity defaults to 20 and will update when the first sync packet arrives.
    }

    public static void updateShader(int sanity) {
        Minecraft client = Minecraft.getInstance();
        if (client.gameRenderer == null) {
            return;
        }
        Identifier actualShaderId = client.gameRenderer.currentPostEffect();
        Identifier targetShader = getTargetShader(sanity);

        if (targetShader != null) {
            if (targetShader.equals(lastAppliedShader)) {
                return;
            }
            ((GameRendererAccessor) client.gameRenderer).invokeSetPostEffect(targetShader);
            lastAppliedShader = targetShader;
        } else {
            if (lastAppliedShader == null && (actualShaderId == null || !isModShader(actualShaderId))) {
                return;
            }
            if (isModShader(actualShaderId)) {
                client.gameRenderer.clearPostEffect();
            }
            lastAppliedShader = null;
        }
    }

    private static Identifier getTargetShader(int sanity) {
        com.kyonshi.config.SanityConfig.ShaderType currentType = com.kyonshi.config.SanityConfig.get().shaderType;

        if (sanity <= 3) {
            return (currentType == com.kyonshi.config.SanityConfig.ShaderType.PIXELATED) ? BITS_STRONG : BLUR_STRONG;
        } else if (sanity <= 6) {
            return (currentType == com.kyonshi.config.SanityConfig.ShaderType.PIXELATED) ? BITS_MONOCHROME : BLUR_MONOCHROME;
        } else if (sanity <= 8) {
            return (currentType == com.kyonshi.config.SanityConfig.ShaderType.PIXELATED) ? BITS_WEAK : BLUR_WEAK;
        } else if (sanity <= 10) {
            return DESATURATE_WEAK;
        }
        return null;
    }

    private static boolean isModShader(Identifier id) {
        if (id == null) return false;
        return id.getNamespace().equals("sanity-level");
    }
}