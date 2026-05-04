package me.anticode.ascendant_arcana.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class SunderedEffect extends StatusEffect {
    public SunderedEffect() {
        super(StatusEffectCategory.HARMFUL, 0x7e14b3);
        addAttributeModifier(EntityAttributes.GENERIC_ARMOR, "c44105f3-3f95-4372-86ab-a5acfbffc710", -0.25D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, "c44105f3-3f95-4372-86ab-a5acfbffc710", -0.25D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        Registry.register(Registries.STATUS_EFFECT, "ascendant_arcana:sundered", this);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
