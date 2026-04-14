package me.anticode.ascendant_arcana.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public record RestorineGrowthFeatureConfig(int floorToCeilingSearchRange, IntProvider width, FloatProvider radiusToHeightRatio, float ceilingPercentage, float restorine_percentage) implements FeatureConfig {
    public static final Codec<RestorineGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.POSITIVE_INT.fieldOf("search_range").forGetter(RestorineGrowthFeatureConfig::floorToCeilingSearchRange),
                    IntProvider.NON_NEGATIVE_CODEC.fieldOf("width").forGetter(RestorineGrowthFeatureConfig::width),
                    FloatProvider.VALUE_CODEC.fieldOf("ratio").forGetter(RestorineGrowthFeatureConfig::radiusToHeightRatio),
                    Codecs.POSITIVE_FLOAT.fieldOf("stalagmite_percentage").forGetter(RestorineGrowthFeatureConfig::ceilingPercentage),
                    Codecs.POSITIVE_FLOAT.fieldOf("restorine_percentage").forGetter(RestorineGrowthFeatureConfig::restorine_percentage))
                .apply(instance, RestorineGrowthFeatureConfig::new));
}
