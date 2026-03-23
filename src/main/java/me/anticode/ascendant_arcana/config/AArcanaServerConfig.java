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
}
