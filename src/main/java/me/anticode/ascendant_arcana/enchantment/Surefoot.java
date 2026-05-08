package me.anticode.ascendant_arcana.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.SwiftSneakEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

public class Surefoot extends TickableAttributeEnchantment {
    public Surefoot() {
        super(false, Rarity.UNCOMMON, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public void initAttributes() {
        addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.4, EntityAttributeModifier.Operation.ADDITION);
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && !(other instanceof SwiftSneakEnchantment) && !(other instanceof Strafe);
    }
}
