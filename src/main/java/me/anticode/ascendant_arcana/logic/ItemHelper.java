package me.anticode.ascendant_arcana.logic;

import me.anticode.ascendant_arcana.api.EnchantedArrow;
import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class ItemHelper {
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

    public static void applyPpeRelicsAndEnchantments(PersistentProjectileEntity persistentProjectileEntity, ItemStack itemStack) {
        int archersGambitLevel = EnchantmentHelper.getLevel(AArcanaEnchantments.ARCHERS_GAMBIT, itemStack);
        int evokersWrathLevel = EnchantmentHelper.getLevel(AArcanaEnchantments.EVOKERS_WRATH, itemStack);
        int rejuvenatingShotLevel = EnchantmentHelper.getLevel(AArcanaEnchantments.REJUVENATING_SHOT, itemStack);
        EnchantedArrow enchantedArrow = (EnchantedArrow) persistentProjectileEntity;
        enchantedArrow.ascendant_arcana$setArchersGambitLevel(archersGambitLevel);
        enchantedArrow.ascendant_arcana$setEvokersWrathLevel(evokersWrathLevel);
        enchantedArrow.ascendant_arcana$setRejuvenatingShotLevel(rejuvenatingShotLevel);
    }

    public static List<EntityAttributeModifier> multiplyAttributeList(List<EntityAttributeModifier> attributes, double multiplier) {
        List<EntityAttributeModifier> newModifiers = new LinkedList<>();
        for (EntityAttributeModifier mod : attributes) {
            double newValue = mod.getValue() * (1 + multiplier);
            EntityAttributeModifier newMod = new EntityAttributeModifier(mod.getId(), mod.getName(), newValue, mod.getOperation());
            newModifiers.add(newMod);
        }
        return newModifiers;
    }

    @FunctionalInterface
    public interface Consumer
    {
        void accept(Enchantment enchantment, ItemStack stack, int level);
    }
}