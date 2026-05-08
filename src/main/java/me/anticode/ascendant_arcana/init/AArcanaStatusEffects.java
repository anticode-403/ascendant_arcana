package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.effect.*;
import net.minecraft.entity.effect.StatusEffect;

public class AArcanaStatusEffects {
    public static StatusEffect ARCHERS_GAMBIT = new ArchersGambitEffect();
    public static StatusEffect CROSS_COUNTER = new CrossCounterEffect();
    public static StatusEffect ECHOING_DAMAGE = new EchoingDamageEffect();
    public static StatusEffect HOBBLED = new HobbledEffect();
    public static StatusEffect SUNDERED = new SunderedEffect();

    public static void initialize() {}
}
