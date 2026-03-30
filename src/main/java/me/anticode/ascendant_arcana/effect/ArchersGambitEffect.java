package me.anticode.ascendant_arcana.effect;

import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ArchersGambitEffect extends StatusEffect {
    public ArchersGambitEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xdba213);
        addAttributeModifier(EntityAttributes_RangedWeapon.HASTE.attribute, "7851b886-b3dc-4da8-b948-6c896ac9fde4", 0.3d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        Registry.register(Registries.STATUS_EFFECT, "ascendant_arcana:archers_gambit", this);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}