package me.anticode.ascendant_arcana.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class Gale extends Enchantment {
    public Gale() {
        super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.FEATHER_FALLING;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && ((ArmorItem)stack.getItem()).getSlotType() == EquipmentSlot.FEET;
    }
}
