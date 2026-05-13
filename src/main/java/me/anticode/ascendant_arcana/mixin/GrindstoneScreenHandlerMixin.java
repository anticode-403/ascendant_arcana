package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.logic.RelicHelper;
import me.anticode.ascendant_arcana.logic.Relics;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneScreenHandlerMixin {
    @Inject(method = "grind", at = @At("HEAD"), cancellable = true)
    private void grind(ItemStack item, int damage, int amount, CallbackInfoReturnable<ItemStack> cir) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(item);
        Map<Relics, Integer> relics = RelicHelper.fromNbt(item.getOrCreateNbt());
        if (enchantments.isEmpty() && relics.isEmpty()) {
            ItemStack itemStack = item.copyWithCount(amount);
            itemStack.removeSubNbt("Damage");
            cir.setReturnValue(itemStack);
            cir.cancel();
        } else {
            ItemStack itemStack = new ItemStack(AArcanaItems.ENCHANTED_SCRAP);
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                int baseCount = switch (entry.getKey().getRarity()) {
                    case COMMON, UNCOMMON -> 1;
                    case RARE -> 3;
                    case VERY_RARE -> 4;
                };
                if (entry.getKey().isCursed()) baseCount = 1;
                else if (entry.getKey().isTreasure()) baseCount += 1;
                itemStack.increment(baseCount * entry.getValue());
            }
            for (Map.Entry<Relics, Integer> entry : relics.entrySet()) {
                itemStack.increment(entry.getValue());
            }

            if (itemStack.getCount() > itemStack.getMaxCount()) itemStack.setCount(itemStack.getMaxCount());
            cir.setReturnValue(itemStack);
            cir.cancel();
        }
    }
}
