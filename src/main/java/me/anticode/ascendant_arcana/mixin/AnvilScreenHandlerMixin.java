package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {
    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;canRepair(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean applyRestorineRepairs(Item instance, ItemStack stack, ItemStack ingredient) {
        if (ingredient.isOf(AArcanaItems.RESTORINE)) return true;
        return instance.canRepair(stack, ingredient);
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int restorineRepairsPartially(int a, int b, @Local(ordinal = 2) ItemStack itemStack3) {
        if (!itemStack3.isOf(AArcanaItems.RESTORINE)) return Math.min(a, b*2); // Base material repairs 50% instead of 25%
        return Math.min(a, b/2); // Restorine repairs 12.5%
    }

    @ModifyArg(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V"))
    private int anvilDoesNotCostLevels(int value) {
        return 0;
    }

    @ModifyReturnValue(method = "canTakeOutput", at = @At("RETURN"))
    private boolean canAlwaysTakeOutput(boolean value) {
        return true;
    }

    @ModifyExpressionValue(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;canCombine(Lnet/minecraft/enchantment/Enchantment;)Z"))
    private boolean enchantmentCapacity(boolean value, @Local(ordinal = 0) ItemStack stack, @Local(ordinal = 2) ItemStack book) {
        Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.get(book);
        int cost = 0;
        for (Enchantment enchantment : bookEnchants.keySet()) {
            if (enchantment.isAcceptableItem(stack) && EnchantmentHelper.isCompatible(EnchantmentHelper.get(stack).keySet(), enchantment)) {
                cost += bookEnchants.get(enchantment) * AArcanaEnchantmentHelper.getEnchantmentCost(enchantment);
            }
        }
        if (AArcanaEnchantmentHelper.getEnchantmentUsage(stack) + cost > AArcanaEnchantmentHelper.getEnchantmentCapacity(stack)) {
            return false;
        }
        return value;
    }
}
