package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {
    @Unique
    private static Enchantment replacement = null;

    @Inject(method = "addEnchantment", at = @At("HEAD"), cancellable = true)
    private static void disableEnchantments(ItemStack stack, EnchantmentLevelEntry entry, CallbackInfo ci) {
        if (!AArcanaEnchantmentHelper.isEnchantmentEnabled(entry.enchantment)) {
            replacement = AArcanaEnchantmentHelper.getReplacement(entry.enchantment, stack);
            if (replacement == null) {
                ci.cancel();
            }
        }
    }

    @ModifyVariable(method = "addEnchantment", at = @At("HEAD"), argsOnly = true)
    private static EnchantmentLevelEntry disableEnchantments(EnchantmentLevelEntry value, ItemStack stack) {
        if (replacement != null) {
            Enchantment temp = replacement;
            replacement = null;
            return new EnchantmentLevelEntry(temp, Math.min(temp.getMaxLevel(), value.level));
        }
        return value;
    }

    @Inject(method = "addEnchantment", at = @At("HEAD"), cancellable = true)
    private static void enchantmentCapacity(ItemStack stack, EnchantmentLevelEntry entry, CallbackInfo ci) {
        if (AscendantArcana.config.single_enchantment_books) {
            if (!EnchantedBookItem.getEnchantmentNbt(stack).isEmpty()) ci.cancel();
        } else if (!AArcanaEnchantmentHelper.testEnchantmentCost(stack, AArcanaEnchantmentHelper.getEnchantmentCost(entry.enchantment, entry.level))) {
            ci.cancel();
        }
    }
}
