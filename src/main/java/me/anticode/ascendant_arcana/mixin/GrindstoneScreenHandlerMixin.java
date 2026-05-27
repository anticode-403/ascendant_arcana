package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
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
            cir.setReturnValue(AArcanaEnchantmentHelper.convertEnchantmentsToScrap(enchantments));
            cir.cancel();
        }
    }
}
