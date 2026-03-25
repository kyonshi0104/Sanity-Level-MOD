package com.kyonshi;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class calmEffect extends MobEffect {
    protected calmEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xe9b8b3);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (entity instanceof Player) {
            ((Player) entity).giveExperiencePoints(1 << amplifier);
        }

        return super.applyEffectTick(level, entity, amplifier);
    }
}