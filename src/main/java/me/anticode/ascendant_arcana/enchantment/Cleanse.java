package me.anticode.ascendant_arcana.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

public class Cleanse extends Enchantment {
    public Cleanse() {
        super(Rarity.VERY_RARE, EnchantmentTarget.WEARABLE, new EquipmentSlot[] {EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ShieldItem;
    }
}
