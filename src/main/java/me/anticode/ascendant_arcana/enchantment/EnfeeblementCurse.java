package me.anticode.ascendant_arcana.enchantment;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

public class EnfeeblementCurse extends TickableAttributeEnchantment
{
    public EnfeeblementCurse()
    {
        super(true, Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public void initAttributes()
    {
        addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, -2, EntityAttributeModifier.Operation.ADDITION);
    }
}