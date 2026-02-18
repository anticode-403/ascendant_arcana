package me.anticode.ascendant_arcana;

import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import me.anticode.ascendant_arcana.init.AArcanaTags;
import net.fabricmc.api.ModInitializer;

public class AscendantArcana implements ModInitializer {

    public static String modID = "ascendant_arcana";

    @Override
    public void onInitialize() {
        AArcanaItems.initialize();
        AArcanaTags.initialize();
        AArcanaBlocks.initialize();
        AArcanaRecipes.initialize();
    }
}
