package com.kyonshi.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "sanity-level") // config/sanity-level.json として保存される
public class SanityConfig implements ConfigData {

    public int gaugeYOffset = 0;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
    public ShaderType shaderType = ShaderType.PIXELATED;

    public enum ShaderType {
        PIXELATED, BLUR
    }

    public static SanityConfig get() {
        return AutoConfig.getConfigHolder(SanityConfig.class).getConfig();
    }

    public static void register() {
        AutoConfig.register(SanityConfig.class, GsonConfigSerializer::new);
    }
}