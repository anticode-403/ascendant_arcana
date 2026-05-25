package me.anticode.ascendant_arcana.enchantment;

import com.google.common.collect.Maps;
import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class TickableAttributeEnchantment extends Enchantment {
    private final boolean isCurse;
    private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers = Maps.newHashMap();
    private final EquipmentSlot[] slotTypes;

    protected TickableAttributeEnchantment(boolean isCurse, Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
        this.slotTypes = slotTypes;
        this.isCurse = isCurse;
        initAttributes();
    }

    public void initAttributes()
    {

    }

    public void onTick(LivingEntity entity, ItemStack stack, int level, EquipmentSlot slot)
    {

    }

    @Override
    public boolean isCursed() {
        return isCurse;
    }

    @Override
    public boolean isTreasure() {
        return isCurse;
    }

    protected void addAttributeModifier(EntityAttribute attribute, double amount, EntityAttributeModifier.Operation operation) {
        EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(UUID.randomUUID(), this::getTranslationKey, amount, operation);
        this.attributeModifiers.put(attribute, entityAttributeModifier);
    }

    public boolean addAttributes(LivingEntity entity, ItemStack stack, EquipmentSlot slot, int level) {
        if (Arrays.stream(slotTypes).toList().contains(slot)) return false;
        return AArcanaEnchantmentHelper.addEnchantmentAttributes(this, attributeModifiers, entity, stack, slot, level);
    }

    public void removeAttributes(LivingEntity entity, EquipmentSlot slot) {
        AArcanaEnchantmentHelper.removeEnchantmentAttributes(this.attributeModifiers, entity, slot);
    }
}
