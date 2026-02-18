package me.anticode.ascendant_arcana;

import me.anticode.ascendant_arcana.init.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AscendantArcana implements ModInitializer {

    public static String modID = "ascendant_arcana";
    public static final Logger LOGGER = LoggerFactory.getLogger(modID);

    @Override
    public void onInitialize() {
        AArcanaItems.initialize();
        AArcanaTags.initialize();
        AArcanaBlocks.initialize();
        AArcanaRecipes.initialize();
        AArcanaAttributes.initialize();
    }
}
