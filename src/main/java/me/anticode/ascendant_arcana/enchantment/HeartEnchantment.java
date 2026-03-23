package me.anticode.ascendant_arcana.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class HeartEnchantment extends Enchantment {
    public HeartEnchantment() {
        super(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && !(other instanceof HeartEnchantment);
    }
}
