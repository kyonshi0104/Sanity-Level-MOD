package com.kyonshi;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;

public class ModPotions {
    private static final List<Holder<Potion>> POTIONS = new ArrayList<>();

    public static final Holder<Potion> CALM_POTION = register("calm",
            new Potion("calm", new MobEffectInstance(SanityLevel.CALM, 3600, 0,true,true)));

    public static final Holder<Potion> LONG_CALM_POTION = register("long_calm",
            new Potion("calm", new MobEffectInstance(SanityLevel.CALM, 9600, 0,true,true)));

    private static Holder<Potion> register(String name, Potion potion) {
        Holder<Potion> holder = Registry.registerForHolder(
                BuiltInRegistries.POTION,
                Identifier.fromNamespaceAndPath("sanity-level", name),
                potion
        );
        POTIONS.add(holder);
        return holder;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(content -> {
            for (Holder<Potion> potion : POTIONS) {
                content.accept(net.minecraft.world.item.alchemy.PotionContents.createItemStack(Items.POTION, potion));
                content.accept(net.minecraft.world.item.alchemy.PotionContents.createItemStack(Items.SPLASH_POTION, potion));
                content.accept(net.minecraft.world.item.alchemy.PotionContents.createItemStack(Items.LINGERING_POTION, potion));
            }
        });

        // 醸造レシピの登録
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.addMix(Potions.AWKWARD, Items.GLOWSTONE, CALM_POTION);
            builder.addMix(CALM_POTION, Items.REDSTONE, LONG_CALM_POTION);
        });
    }
}