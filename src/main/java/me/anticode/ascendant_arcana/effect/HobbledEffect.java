package me.anticode.ascendant_arcana.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class HobbledEffect extends StatusEffect {
    public HobbledEffect() {
        super(StatusEffectCategory.HARMFUL, 0x7e14b3);
        addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "c44105f3-3f95-4372-86ab-a5acfbffc710", -0.1D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        Registry.register(Registries.STATUS_EFFECT, "ascendant_arcana:hobbled", this);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
