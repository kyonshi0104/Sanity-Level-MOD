package com.kyonshi;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.function.Function;

public class ModItems {
    public static final Consumable MILK_CARAMEL_CONSUMABLE = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(SanityLevel.SANITY_RECOVERY, 1, 3,false,false,false), 1.0f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(SanityLevel.CALM, 3 * 20, 0,false,false,true), 1.0f))
            .build();

    public static final FoodProperties MILK_CARAMEL_COMPONENT = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.2f)
            .alwaysEdible()
            .build();

    // アイテム登録
    public static final Item CARAMEL = register("caramel", Item::new, new Item.Properties().food(MILK_CARAMEL_COMPONENT));

    public static final Item MILK_CARAMEL = register("milk_caramel",
            Item::new,
            new Item.Properties().food(MILK_CARAMEL_COMPONENT, MILK_CARAMEL_CONSUMABLE).stacksTo(16));

    public static final Item KNOT_OF_SERENITY = register("knot_of_serenity",
            KnotOfSerenityItem::new,
            new Item.Properties().fireResistant().stacksTo(1).rarity(Rarity.EPIC));

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("sanity-level", name));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register(itemGroup -> {
                    itemGroup.accept(CARAMEL);
                    itemGroup.accept(MILK_CARAMEL);
                });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(itemGroup -> {
                    itemGroup.accept(KNOT_OF_SERENITY);
                });
    }
}