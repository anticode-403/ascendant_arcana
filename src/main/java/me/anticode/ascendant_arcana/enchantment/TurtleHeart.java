package me.anticode.ascendant_arcana.enchantment;

import com.google.common.collect.Maps;
import me.anticode.ascendant_arcana.init.AArcanaAttributes;
import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.UUID;

public class TurtleHeart extends HeartEnchantment {
    private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers = Maps.newHashMap();

    public TurtleHeart() {
        super();
        initAttributes();
    }


    public void initAttributes()
    {
        EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(UUID.randomUUID(), this::getTranslationKey, -0.25, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        this.attributeModifiers.put(AArcanaAttributes.DAMAGE_TAKEN, entityAttributeModifier);
    }

    public boolean addAttributes(LivingEntity entity, ItemStack stack, EquipmentSlot slot, int level)
    {
        return AArcanaEnchantmentHelper.addEnchantmentAttributes(this, attributeModifiers, entity, stack, slot, level);
    }

    public void removeAttributes(LivingEntity entity, EquipmentSlot slot)
    {
        AArcanaEnchantmentHelper.removeEnchantmentAttributes(attributeModifiers, entity, slot);
    }
}
