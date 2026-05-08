package me.anticode.ascendant_arcana.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CrossCounterEffect extends StatusEffect {
    public CrossCounterEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x00cbd6);
        addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "c44105f3-3f95-4372-86ab-a5acfbffc710", 4D, EntityAttributeModifier.Operation.ADDITION);
        Registry.register(Registries.STATUS_EFFECT, "ascendant_arcana:cross_counter", this);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}
