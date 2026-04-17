package me.anticode.ascendant_arcana.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.List;
import java.util.Map;

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

    @Comment("Add restorine to any chests containing amethyst shards or diamonds")
    public boolean add_restorine_to_chests = true;

    @Comment("Add unique boss drops, like the Warden Heart. Disabling this will make some enchantments unobtainable.")
    public boolean add_boss_drops = true;

    @Comment("The minimum amount of power (bookshelves and enchanted books within range of an enchanting table) to enchant.")
    public int minimum_enchanting_power = 0;

    @Comment("The minimum amount of power required to enchant uncommon enchantments.")
    public int uncommon_enchanting_power = 22;

    @Comment("The minimum amount of power required to enchant rare enchantments.")
    public int rare_enchanting_power = 50;

    @Comment("The minimum amount of power required to enchant very rare enchantments.")
    public int very_rare_enchanting_power = 80;

    @Comment("""
            Ascendant Arcana disables many vanilla enchantments because they stress the capacity system too much with
            'required' enchantments. Enchantments are generally meant to be more interesting and meaningfully impactful
            but this list is configurable so you can enable or disable whatever you want.
            
            Do note that enchantments on this list by default DO NOT come with recipes, so in order to see them in
            the Enchanting Table you must create your own enchantment recipes for them with a datapack.""")
    public List<String> disabled_enchantments = List.of(
            "minecraft:protection",
            "minecraft:sharpness",
            "minecraft:efficiency",
            "minecraft:quick_charge",
            "minecraft:power",
            "minecraft:bane_of_arthropods",
            "minecraft:blast_protection",
            "minecraft:projectile_protection",
            "minecraft:fire_protection",
            "minecraft:smite",
            "minecraft:impaling",
            "minecraft:mending",
            "minecraft:unbreaking",
            "majruszsenchantments:misanthropy",
            "majruszsenchantments:gold_fuelled",
            "majruszsenchantments:smelter",
            "majruszsenchantments:magic_protection",
            "majruszsenchantments:dodge",
            "majruszsenchantments:enlightenment",
            "majruszsenchantments:immortality"
    );

    @Comment("Items which have their base relic capacity value overwritten.")
    public Map<String, Integer> base_relic_capacity_overrides = Map.of(
            "minecraft:crossbow",
            1
    );

    @Comment("Items which have their base enchantment capacity overwritten")
    public Map<String, Integer> base_enchantment_capacity_overrides = Map.of(
            "minecraft:bow",
            20,
            "minecraft:crossbow",
            20
    );
}
