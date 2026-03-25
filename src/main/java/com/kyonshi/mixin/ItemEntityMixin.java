package com.kyonshi.mixin;

import com.kyonshi.KnotOfSerenityItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.tags.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow public abstract ItemStack getItem();

    // 提示されたソースにあるメソッド名「hurtServer」に注入します
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void cancelExplosionDamage(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            if (this.getItem().getItem() instanceof KnotOfSerenityItem) {
                cir.setReturnValue(false); // ダメージ処理を中断
            }
        }
    }
}