package me.anticode.ascendant_arcana.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;

public class Smelting extends Enchantment {
    public Smelting() {
        super(Rarity.RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && !(other instanceof SilkTouchEnchantment);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof MiningToolItem && !(stack.getItem() instanceof HoeItem);
    }
}
