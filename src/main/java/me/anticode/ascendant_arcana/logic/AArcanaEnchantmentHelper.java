package me.anticode.ascendant_arcana.logic;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AArcanaEnchantmentHelper {
    public static int getEnchantmentCost(Enchantment enchantment) {
        return switch (enchantment.getRarity()) {
            case VERY_RARE -> 10;
            case RARE -> 5;
            case UNCOMMON -> 3;
            case COMMON -> 1;
        };
    }

    public static int getBaseEnchantmentCapacity(Item item) {
        if (item instanceof ArmorItem armorItem) {
            return armorItem.getEnchantability();
        } else if (item instanceof ToolItem toolItem) {
            return toolItem.getEnchantability();
        }
        return 10;
    }

    public static boolean isEnchantmentAllowed(Identifier identifier) {
        if (identifier == null) {
            return true;
        }
        // TODO: Finish this implementation
        return false;
    }

    public static boolean isEnchantmentAllowed(Enchantment enchantment) {
        return isEnchantmentAllowed(Registries.ENCHANTMENT.getId(enchantment));
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
        randomBytes[8] |= 0x80;
        return UUID.nameUUIDFromBytes(randomBytes);
    }
}
