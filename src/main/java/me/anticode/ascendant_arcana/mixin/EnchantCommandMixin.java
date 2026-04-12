package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.EnchantCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnchantCommand.class)
public class EnchantCommandMixin {
    @Unique
    private static ItemStack cachedStack = null;

    @ModifyVariable(method = "execute", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
    private static ItemStack enchantmentCapacity(ItemStack value) {
        cachedStack = value;
        return value;
    }

    @ModifyExpressionValue(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;isCompatible(Ljava/util/Collection;Lnet/minecraft/enchantment/Enchantment;)Z"))
    private static boolean enchantmentCapacity(boolean value, @Local ItemStack stack, @Local Enchantment enchantment, @Local(argsOnly = true) int level) {
        if (AArcanaEnchantmentHelper.getEnchantmentUsage(stack) + (AArcanaEnchantmentHelper.getEnchantmentCost(enchantment) * level) > AArcanaEnchantmentHelper.getEnchantmentCapacity(stack)) {
            value = false;
        }
        cachedStack = null;
        return value;
    }
}