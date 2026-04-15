package com.kyonshi.mixin;

import com.kyonshi.util.SanityManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderMan.class)
public abstract class EnderManMixin {

    @Inject(method = "setTarget", at = @At("HEAD"))
    private void onSetTarget(LivingEntity target, CallbackInfo ci) {
        if (target instanceof ServerPlayer player) {
            if (player.tickCount % 20 == 0) {
                SanityManager.addSanity(player, -5);
            }
        }
    }
}