package me.anticode.ascendant_arcana.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "server")
public class AArcanaServerConfig implements ConfigData {
    @Comment("Multiplies the base enchantment capacity and all gains by this value. 1.0 is recommended.")
    public double capacity_multiplier = 1.0D;

    @Comment("Default relic capacity of an item, recommended is 2 but 1 could provide for more interesting gameplay.")
    public int base_relic_capacity = 2;

    @Comment("Some vanilla mobs drop relics on death, like Witches, Wither Skeletons, and bosses. Recommended.")
    public boolean add_relics_to_entities = true;

    @Comment("Relics are automatically added to ANY chest loot table containing an enchanted book. Recommended.")
    public boolean add_relics_to_chests = true;

    @Comment("Add unique boss drops, like the Warden Heart. Disabling this will make some enchantments unobtainable.")
    public boolean add_boss_drops = true;

    @Comment("The minimum amount of power (bookshelves and enchanted books within range of an enchanting table) to enchant")
    public int minimum_enchanting_power = 0;

    @Comment("The minimum amount of power required to enchant uncommon enchantments")
    public int uncommon_enchanting_power = 22;

    @Comment("The minimum amount of power required to enchant rare enchantments")
    public int rare_enchanting_power = 50;

    @Comment("The minimum amount of power required to enchant very rare enchantments")
    public int very_rare_enchanting_power = 80;
}
