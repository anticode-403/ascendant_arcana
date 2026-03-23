package me.anticode.ascendant_arcana;

import me.anticode.ascendant_arcana.config.AArcanaServerConfig;
import me.anticode.ascendant_arcana.config.AArcanaServerConfigWrapper;
import me.anticode.ascendant_arcana.init.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AscendantArcana implements ModInitializer {

    public static String modID = "ascendant_arcana";
    public static final Logger LOGGER = LoggerFactory.getLogger(modID);
    public static AArcanaServerConfig config;

    @Override
    public void onInitialize() {
        AArcanaItems.initialize();
        AArcanaTags.initialize();
        AArcanaBlocks.initialize();
        AArcanaRecipes.initialize();
        AArcanaAttributes.initialize();
        AArcanaEnchantments.initialize();
        AArcanaStatusEffects.initialize();

        AutoConfig.register(AArcanaServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        config = AutoConfig.getConfigHolder(AArcanaServerConfigWrapper.class).getConfig().server;
    }
}
