package me.anticode.ascendant_arcana.logic;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemUtil {
    public static void forEachEnchantment(Consumer consumer, ItemStack stack, boolean allowEmpty)
    {
        if(!stack.isEmpty() || allowEmpty)
        {
            NbtList listTag = stack.getEnchantments();

            for(int i = 0; i < listTag.size(); ++i)
            {
                String string = listTag.getCompound(i).getString("id");
                int j = listTag.getCompound(i).getInt("lvl");
                Registries.ENCHANTMENT.getOrEmpty(Identifier.tryParse(string)).ifPresent((enchantment)->
                        consumer.accept(enchantment, stack, j));
            }
        }
    }

    @FunctionalInterface
    public interface Consumer
    {
        void accept(Enchantment enchantment, ItemStack stack, int level);
    }
}