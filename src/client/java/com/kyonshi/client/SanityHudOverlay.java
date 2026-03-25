package com.kyonshi.client;

import com.kyonshi.SanityLevel;
import com.kyonshi.SanityLevelClient;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class SanityHudOverlay {
    private static final Identifier SANITY_ICONS = Identifier.fromNamespaceAndPath(SanityLevel.MOD_ID, "textures/gui/sanity_icons.png");
    private static final Identifier SANITY_ICONS_CALM = Identifier.fromNamespaceAndPath(SanityLevel.MOD_ID, "textures/gui/sanity_icons_calm.png");

    public static void render(GuiGraphics drawContext, DeltaTracker tickDelta) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui) return;

        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();

        // 基準位置（ホットバーの左端、体力の位置）
        int x = width / 2 - 91;
        int y = height - 39;


        float maxHealth = client.player.getMaxHealth();
        maxHealth += client.player.getAbsorptionAmount();
        int healthRows = (int) Math.ceil(maxHealth / 20.0f);

        // 位置計算
        int healthOffset = 0;
        for (int row = 1; row < healthRows; row++) {
            if (healthRows <= 2) {
                healthOffset += 10;
            } else {
                int interval = 0;
                if (healthRows > 8) {
                    interval = 3;
                } else {
                    interval = 10 - (healthRows - 2);
                }

                healthOffset += interval;
            }
        }
        y -= healthOffset;
        // 防御力ゲージを考慮
        int armor = client.player.getArmorValue();
        if (armor > 0) {
            y -= 10;
        }

        y -= 10;
        // -----------------------

        int sanity = SanityLevelClient.currentSanity;
        boolean calm = client.player.hasEffect(SanityLevel.CALM);
        Identifier SANITY_ICONS_C = SANITY_ICONS;

        if (calm) {
            SANITY_ICONS_C = SANITY_ICONS_CALM;
        }


        for (int i = 0; i < 10; i++) {
            int iconX = x + (i * 8);
            int iconY = y;

            if (sanity <= 5 && !calm) {
                iconY += client.level.random.nextInt(3) - 1;
            }

            drawContext.blit(RenderPipelines.GUI_TEXTURED, SANITY_ICONS_C, iconX, iconY, 0f, 0f, 9, 9, 27, 9);

            if (sanity > i * 2 + 1) {
                drawContext.blit(RenderPipelines.GUI_TEXTURED, SANITY_ICONS_C, iconX, iconY, 18f, 0f, 9, 9, 27, 9);
            } else if (sanity > i * 2) {
                drawContext.blit(RenderPipelines.GUI_TEXTURED, SANITY_ICONS_C, iconX, iconY, 9f, 0f, 9, 9, 27, 9);
            }
        }
    }

    public static void register() {
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HEALTH_BAR,
                Identifier.fromNamespaceAndPath(SanityLevel.MOD_ID, "sanity_display"),
                SanityHudOverlay::render
        );
    }
}