package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.api.EnchantedTrident;
import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TridentItem.class)
public abstract class TridentItemMixin {
    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/TridentEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"))
    private void applyEnchantmentValues(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci,
                                        @Local TridentEntity tridentEntity) {
        int ambushLevel = EnchantmentHelper.getLevel(AArcanaEnchantments.AMBUSH, stack);
        int lifetideLevel = EnchantmentHelper.getLevel(AArcanaEnchantments.LIFETIDE, stack);

        EnchantedTrident enchantedTrident = (EnchantedTrident) tridentEntity;
        enchantedTrident.ascendant_arcana$setAmbushLevel(ambushLevel);
        enchantedTrident.ascendant_arcana$setLifetideLevel(lifetideLevel);
    }
}
