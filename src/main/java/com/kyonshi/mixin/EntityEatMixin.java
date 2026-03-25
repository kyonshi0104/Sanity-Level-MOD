package com.kyonshi.mixin;

import com.kyonshi.util.SanityManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EntityEatMixin {

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void onCompleteUsing(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!entity.level().isClientSide() && entity instanceof ServerPlayer player) {

            // 使用完了アイテム取得
            ItemStack stack = player.getUseItem();
            var item = stack.getItem();

            if (item == Items.ROTTEN_FLESH || item == Items.SPIDER_EYE || item == Items.POISONOUS_POTATO) {
                SanityManager.addSanity(player, -3);
            }
            else if (item == Items.BEEF || item == Items.PORKCHOP || item == Items.CHICKEN ||
                    item == Items.MUTTON || item == Items.RABBIT || item == Items.SUSPICIOUS_STEW) {
                SanityManager.addSanity(player, -1);
            }

            if (item == Items.COOKED_BEEF || item == Items.COOKED_PORKCHOP || item == Items.COOKED_CHICKEN ||
                    item == Items.COOKED_MUTTON || item == Items.COOKED_RABBIT) {
                SanityManager.addSanity(player, 2);
            } else if (item == Items.RABBIT_STEW || item == Items.MUSHROOM_STEW || item == Items.BREAD || item == Items.COOKIE ||
                    item == Items.CAKE || item == Items.PUMPKIN_PIE || item == Items.HONEY_BOTTLE)
            {
                SanityManager.addSanity(player, 3);
            }
        }
    }
}