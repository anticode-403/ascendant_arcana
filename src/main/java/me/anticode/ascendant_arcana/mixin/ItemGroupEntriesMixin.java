package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(ItemGroup.EntriesImpl.class)
public class ItemGroupEntriesMixin {
    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void disableEnchantments(ItemStack stack, ItemGroup.StackVisibility visibility, CallbackInfo ci) {
        if (stack.isEmpty()) {
            ci.cancel();
        }
        Map<Enchantment, Integer> newMap = new LinkedHashMap<>();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        for (Enchantment enchantment : enchantments.keySet()) {
            if (AArcanaEnchantmentHelper.isEnchantmentEnabled(enchantment)) {
                newMap.put(enchantment, enchantments.get(enchantment));
            }
        }
        EnchantmentHelper.set(newMap, stack);
    }

    @Inject(method = "add", at = @At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V"), cancellable = true)
    private void avoidIllegalState(ItemStack stack, ItemGroup.StackVisibility visibility, CallbackInfo ci) {
        ci.cancel();
    }
}
