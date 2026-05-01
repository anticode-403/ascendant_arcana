package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.anticode.ascendant_arcana.AscendantArcana;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @ModifyReturnValue(method = "getRarity", at = @At("RETURN"))
    private Enchantment.Rarity modifyEnchantmentRarities(Enchantment.Rarity original) {
        Enchantment enchantment = (Enchantment)(Object)this;
        Identifier id = Registries.ENCHANTMENT.getId(enchantment);
        if (AscendantArcana.config.overwritten_rarities.containsKey(id.toString())) {
            return switch(AscendantArcana.config.overwritten_rarities.get(id.toString())) {
                case 2 -> Enchantment.Rarity.UNCOMMON;
                case 3 -> Enchantment.Rarity.RARE;
                case 4 -> Enchantment.Rarity.VERY_RARE;
                default -> Enchantment.Rarity.COMMON;
            };
        }
        return original;
    }
}
