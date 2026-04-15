package com.kyonshi.mixin;

import com.kyonshi.util.SanityManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void onStartUsingItem(InteractionHand hand, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer player) {
            if (SanityManager.isSanityCritical(player)) {
                SanityManager.forceDropActiveItem(player);
                ci.cancel();
            }
        }
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    private void onUpdateUsingItem(CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer player) {
            if (SanityManager.isSanityCritical(player)) {
                SanityManager.forceDropActiveItem(player);
                return;
            }
        }
    }
}