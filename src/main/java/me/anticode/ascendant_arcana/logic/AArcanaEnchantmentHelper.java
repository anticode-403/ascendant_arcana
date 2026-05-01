package me.anticode.ascendant_arcana.logic;

import me.anticode.ascendant_arcana.AscendantArcana;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

public class AArcanaEnchantmentHelper {
    public static String ENCHANTMENT_CAPACITY_KEY = "AArcanaEnchantmentCapacity";

    public static int getEnchantmentCost(Enchantment enchantment) {
        return switch (enchantment.getRarity()) {
            case VERY_RARE -> 5;
            case RARE -> 3;
            case UNCOMMON -> 2;
            case COMMON -> 1;
        };
    }

    public static int getEnchantmentCost(Enchantment enchantment, int level) {
        return getEnchantmentCost(enchantment) * level;
    }

    public static int getEnchantmentUsage(ItemStack stack) {
        if (!stack.hasEnchantments() && !(stack.getItem() instanceof EnchantedBookItem)) return 0;
        int cost = 0;
        for (Map.Entry<Enchantment, Integer> enchantInstance : EnchantmentHelper.get(stack).entrySet()) {
            cost += getEnchantmentCost(enchantInstance.getKey()) * enchantInstance.getValue();
        }
        return cost;
    }

    public static boolean testEnchantmentCost(ItemStack stack, int extra) {
//        if (stack.getItem())
        return getEnchantmentUsage(stack) + extra <= getEnchantmentCapacity(stack);
    }

    public static int getBaseEnchantmentCapacity(Item item) {
        int base_capacity = (int)Math.floor(10 * AscendantArcana.config.capacity_multiplier);
        if (item instanceof ArmorItem armorItem) {
            base_capacity = armorItem.getEnchantability();
        } else if (item instanceof ToolItem toolItem) {
            base_capacity = toolItem.getEnchantability();
        } else if (item.getEnchantability() > 10) {
            base_capacity = item.getEnchantability();
        }
        if (AscendantArcana.config.base_enchantment_capacity_overrides.containsKey(Registries.ITEM.getId(item).toString())) {
            base_capacity = AscendantArcana.config.base_enchantment_capacity_overrides.get(Registries.ITEM.getId(item).toString());
        }
        return base_capacity;
    }

    public static int getEnchantmentCapacity(ItemStack stack) {
        if (stack.getOrCreateNbt().contains(ENCHANTMENT_CAPACITY_KEY)) {
            return stack.getOrCreateNbt().getInt(ENCHANTMENT_CAPACITY_KEY);
        }
        return getBaseEnchantmentCapacity(stack.getItem());
    }

    public static int getRequiredEnchantmentPower(Enchantment enchantment) {
        return switch (enchantment.getRarity()) {
            case COMMON -> AscendantArcana.config.minimum_enchanting_power;
            case UNCOMMON -> AscendantArcana.config.uncommon_enchanting_power;
            case RARE -> AscendantArcana.config.rare_enchanting_power;
            case VERY_RARE -> AscendantArcana.config.very_rare_enchanting_power;
        };
    }

    public static void setEnchantmentCapacity(ItemStack stack, int value) {
        stack.getOrCreateNbt().putInt(ENCHANTMENT_CAPACITY_KEY, value);
    }

    public static boolean isEnchantmentEnabled(Identifier identifier) {
        AscendantArcana.initializeConfigIfNull();
        if (identifier == null) {
            return true;
        }
        return !AscendantArcana.config.disabled_enchantments.contains(identifier.toString());
    }

    public static boolean isEnchantmentEnabled(Enchantment enchantment) {
        return isEnchantmentEnabled(Registries.ENCHANTMENT.getId(enchantment));
    }

    public static Enchantment getReplacement(Enchantment enchantment, ItemStack stack) {
        List<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment entry : Registries.ENCHANTMENT) {
            if (stack.isOf(Items.ENCHANTED_BOOK) || entry.isAcceptableItem(stack)) {
                enchantments.add(entry);
            }
        }
        if (enchantments.isEmpty()) {
            return null;
        }
        int index = Registries.ENCHANTMENT.getId(enchantment).hashCode() % enchantments.size();
        if (index < 0) {
            index += enchantments.size();
        }
        return enchantments.get(index);
    }

    public static void removeEnchantmentAttributes(Map<EntityAttribute, EntityAttributeModifier> attributeModifiers, LivingEntity entity, EquipmentSlot slot) {
        for(Map.Entry<EntityAttribute, EntityAttributeModifier> attributeEntry : attributeModifiers.entrySet())
        {
            UUID slotID = getUUID(slot.toString());
            EntityAttributeInstance entityAttributeInstance = entity.getAttributes().getCustomInstance(attributeEntry.getKey());
            if(entityAttributeInstance != null)
            {
                EntityAttributeModifier mod = entityAttributeInstance.getModifier(slotID);
                if(mod != null)
                    entityAttributeInstance.removeModifier(mod);
                else
                    System.out.println("RIP modifier: " + entityAttributeInstance.getAttribute().getTranslationKey());
            }
        }
    }

    public static boolean addEnchantmentAttributes(Enchantment enchantment, Map<EntityAttribute, EntityAttributeModifier> attributeModifiers, LivingEntity entity, ItemStack stack, EquipmentSlot slot, int level) {

        if(attributeModifiers.isEmpty() || stack.isEmpty()) return false;

        for(Map.Entry<EntityAttribute, EntityAttributeModifier> attributeEntry : attributeModifiers.entrySet())
        {
            EntityAttributeInstance entityAttributeInstance = entity.getAttributes().getCustomInstance(attributeEntry.getKey());
            if(entityAttributeInstance != null)
            {
                EntityAttributeModifier mod = attributeEntry.getValue();
                entityAttributeInstance.removeModifier(mod);
                entityAttributeInstance.addTemporaryModifier(new EntityAttributeModifier(getUUID(slot.toString()), enchantment.getTranslationKey() + " " + level, mod.getValue() * (double) level, mod.getOperation()));

            }
        }
        return true;
    }

    public static UUID getUUID(String slotID) {
        Random random = new Random(slotID.hashCode());

        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        randomBytes[6] &= 0x0f;
        randomBytes[6] |= 0x40;
        randomBytes[8] &= 0x3f;
        randomBytes[8] |= (byte) 0x80;
        return UUID.nameUUIDFromBytes(randomBytes);
    }
}
