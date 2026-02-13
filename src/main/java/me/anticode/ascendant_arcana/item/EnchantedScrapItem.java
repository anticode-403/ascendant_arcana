package me.anticode.ascendant_arcana.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnchantedScrapItem extends Item {
    public EnchantedScrapItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
