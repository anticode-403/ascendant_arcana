package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.enchantment.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class AArcanaEnchantments {
    public static Enchantment AMBUSH = register(new Ambush(), "ambush");
    public static Enchantment ARCHERS_GAMBIT = register(new ArchersGambit(), "archers_gambit");
    public static Enchantment CLEANSE = register(new Cleanse(), "cleanse");
    public static Enchantment CROSS_COUNTER = register(new CrossCounter(), "cross_counter");
    public static Enchantment DEBILITATING_CHAIN = register(new DebilitatingChain(), "debilitating_chain");
    public static Enchantment DEFLECT = register(new Deflect(), "deflect");
    public static Enchantment EVOKERS_WRATH = register(new EvokersWrath(), "evokers_wrath");
    public static Enchantment HELLWALKER = register(new HellWalker(), "hellwalker");
    public static Enchantment HOBBLING_SHOT = register(new HobblingShot(), "hobbling_shot");
    public static Enchantment LIFETIDE = register(new Lifetide(), "lifetide");
    public static Enchantment PINCUSHION = register(new Pincushion(), "pincushion");
    public static Enchantment PROTECTIVE_ECHO = register(new ProtectiveEcho(), "protective_echo");
    public static Enchantment REJUVENATING_SHOT = register(new RejuvenatingShot(), "rejuvenating_shot");
    public static Enchantment RICOCHET = register(new Ricochet(), "ricochet");
    public static Enchantment SMELTING = register(new Smelting(), "smelting");
    public static Enchantment SOUL_BURST = register(new SoulBurst(), "soul_burst");
    public static Enchantment STRAFE = register(new Strafe(), "strafe");
    public static Enchantment SUNDERING = register(new Sundering(), "sundering");

    public static Enchantment ALCHEMISTS_HEART = register(new HeartEnchantment(), "alchemists_heart");
    public static Enchantment NETHER_HEART = register(new HeartEnchantment(), "heart_of_the_nether");
    public static Enchantment COLDHEART = register(new HeartEnchantment(), "coldheart");
    public static Enchantment STORM_HEART = register(new HeartEnchantment(), "heart_of_the_storm");
    public static Enchantment BLADEHEART =  register(new HeartEnchantment(), "bladeheart");
    public static Enchantment WITCH_HEART = register(new HeartEnchantment(), "witch_heart");
    public static Enchantment TURTLE_HEART = register(new TurtleHeart(), "heart_of_the_turtle");

    public static Enchantment INACCURACY_CURSE = register(new InaccuracyCurse(), "inaccuracy_curse");


    public static Enchantment register(Enchantment enchantment, String id) {
        Identifier enchantmentId = new Identifier(AscendantArcana.modID, id);
        return Registry.register(Registries.ENCHANTMENT, enchantmentId, enchantment);
    }

    public enum IndirectHeartDamageTypes {
        NETHER,
        COLD,
        STORM
    }

    public static void initialize() {}
}
