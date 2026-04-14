package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.worldgen.feature.RestorineGrowthFeature;
import me.anticode.ascendant_arcana.worldgen.feature.RestorineGrowthFeatureConfig;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;

public class AArcanaFeatures {
    public static final Identifier RESTORINE_FEATURE_ID = Identifier.of(AscendantArcana.modID, "restorine_growth");
    public static final RestorineGrowthFeature RESTORINE_FEATURE = new RestorineGrowthFeature(RestorineGrowthFeatureConfig.CODEC);

    public static void initialize() {
        Registry.register(Registries.FEATURE, RESTORINE_FEATURE_ID, RESTORINE_FEATURE);

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.RAW_GENERATION,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, RESTORINE_FEATURE_ID));
    }
}
