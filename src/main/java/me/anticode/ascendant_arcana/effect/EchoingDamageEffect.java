package me.anticode.ascendant_arcana.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EchoingDamageEffect extends StatusEffect {
    public EchoingDamageEffect() {
        super(StatusEffectCategory.HARMFUL, 0x07522f);
        Registry.register(Registries.STATUS_EFFECT, "ascendant_arcana:echoing_damage", this);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}
