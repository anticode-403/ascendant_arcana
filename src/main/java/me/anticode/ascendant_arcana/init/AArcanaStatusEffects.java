package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.effect.ArchersGambitEffect;
import me.anticode.ascendant_arcana.effect.EchoingDamageEffect;
import me.anticode.ascendant_arcana.effect.HobbledEffect;
import net.minecraft.entity.effect.StatusEffect;

public class AArcanaStatusEffects {
    public static StatusEffect ARCHERS_GAMBIT = new ArchersGambitEffect();
    public static StatusEffect ECHOING_DAMAGE = new EchoingDamageEffect();
    public static StatusEffect HOBBLED = new HobbledEffect();

    public static void initialize() {}
}
