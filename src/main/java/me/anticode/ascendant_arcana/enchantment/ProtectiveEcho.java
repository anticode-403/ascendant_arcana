package me.anticode.ascendant_arcana.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class ProtectiveEcho extends Enchantment {
    public ProtectiveEcho() {
        super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }
}
