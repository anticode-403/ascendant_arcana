package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.screenhandler.AArcanaEnchantingScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class AArcanaScreenHandlers {
    public static final ScreenHandlerType<AArcanaEnchantingScreenHandler> ENCHANTING = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(AscendantArcana.modID, "enchanting_table"),
            new ScreenHandlerType<>(AArcanaEnchantingScreenHandler::new, FeatureSet.empty())
    );

    public static void initialize () {

    }
}
