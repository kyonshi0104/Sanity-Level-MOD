package com.kyonshi.mixin;

import com.kyonshi.util.SanityManager;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPlayerTick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        int sanity = SanityManager.getSanity(player);

        if (sanity <= 6) {
            if (player.tickCount % 20 == 0 && player.getRandom().nextFloat() <= 0.05f) {
                SanityManager.dropCurrentItem(player);
            }
        }

        if (sanity <= 3) {
            if (player.tickCount % 20 == 0 && player.getRandom().nextFloat() <= 0.15f) {
                SanityManager.dropCurrentItem(player);
            }
        }
    }
}