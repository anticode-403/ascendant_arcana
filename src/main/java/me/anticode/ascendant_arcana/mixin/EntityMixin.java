package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(at = @At("HEAD"), method = "updateSwimming")
    private void updateSwimming(CallbackInfo cbi) {
        if((Entity) ((Object)this) instanceof LivingEntity living && living.isSwimming()) {
            if(EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.DEPTHS_CURSE, living) > 0) {
                living.setSwimming(false);
            }
        }
    }
}
