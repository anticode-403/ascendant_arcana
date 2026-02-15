package me.anticode.ascendant_arcana.client;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaTags;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class AscendantArcanaDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(AATagProvider::new);
        pack.addProvider(AAModelProvider::new);
        pack.addProvider(AALanguageProvider::new);
    }

    public static class AAModelProvider extends FabricModelProvider {
//        public static final Model RESTORINE_CROSS = new Model(
//
//        );

        public AAModelProvider(FabricDataOutput output) {
            super(output);
        }

        public final void registerRestorine(BlockStateModelGenerator blockStateModelGenerator, Block block) {
            blockStateModelGenerator.excludeFromSimpleItemModelGeneration(block);
            blockStateModelGenerator.blockStateCollector.accept(
                    VariantsBlockStateSupplier.create(
                            block,
                            BlockStateVariant.create().put(
                                    VariantSettings.MODEL,
                                    Models.CROSS.upload(
                                            block,
                                            TextureMap.cross(block),
                                            blockStateModelGenerator.modelCollector
                                    )
                            )
                    ).coordinate(blockStateModelGenerator.createUpDefaultFacingVariantMap())
            );
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

            blockStateModelGenerator.registerCubeAllModelTexturePool(AArcanaBlocks.BUDDING_RESTORINE);

            // Restorine clusters
            registerRestorine(blockStateModelGenerator, AArcanaBlocks.SMALL_RESTORINE_BUD);
            registerRestorine(blockStateModelGenerator, AArcanaBlocks.MEDIUM_RESTORINE_BUD);
            registerRestorine(blockStateModelGenerator, AArcanaBlocks.LARGE_RESTORINE_BUD);
            registerRestorine(blockStateModelGenerator, AArcanaBlocks.RESTORINE_CLUSTER);
            registerRestorine(blockStateModelGenerator, AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            itemModelGenerator.register(AArcanaItems.INFUSION_SMITHING_TEMPLATE, Models.GENERATED);

            itemModelGenerator.register(AArcanaItems.ENCHANTED_SCRAP, Models.GENERATED);
            itemModelGenerator.register(AArcanaItems.RESTORINE, Models.GENERATED);

            // Relics
            itemModelGenerator.register(AArcanaItems.ASCENDANT_RELIC, Models.GENERATED);
            itemModelGenerator.register(AArcanaItems.AWAKENED_RELIC, Models.GENERATED);
            itemModelGenerator.register(AArcanaItems.WAKING_RELIC, Models.GENERATED);
            itemModelGenerator.register(AArcanaItems.STIRRING_RELIC, Models.GENERATED);
            itemModelGenerator.register(AArcanaItems.DORMANT_RELIC, Models.GENERATED);

            // Restorine Clusters
//            itemModelGenerator.register(AArcanaBlocks.SMALL_RESTORINE_BUD.asItem(), Models.GENERATED);
//            itemModelGenerator.register(AArcanaBlocks.MEDIUM_RESTORINE_BUD.asItem(), Models.GENERATED);
//            itemModelGenerator.register(AArcanaBlocks.LARGE_RESTORINE_BUD.asItem(), Models.GENERATED);
//            itemModelGenerator.register(AArcanaBlocks.RESTORINE_CLUSTER.asItem(), Models.GENERATED);
//            itemModelGenerator.register(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER.asItem(), Models.GENERATED);
        }
    }

    public static class AATagProvider extends FabricTagProvider<Item> {

        public AATagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.ITEM, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(AArcanaTags.Items.RELICS)
                    .add(AArcanaItems.DORMANT_RELIC)
                    .add(AArcanaItems.STIRRING_RELIC)
                    .add(AArcanaItems.WAKING_RELIC)
                    .add(AArcanaItems.AWAKENED_RELIC)
                    .add(AArcanaItems.ASCENDANT_RELIC);
        }
    }

    public static class AALanguageProvider extends FabricLanguageProvider {

        protected AALanguageProvider(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            String template_id = AArcanaItems.INFUSION_SMITHING_TEMPLATE_ID;
            translationBuilder.add("item." + AscendantArcana.modID + "." + template_id + ".applies_to", "Armor, Tools, and Weapons");
            translationBuilder.add("item." + AscendantArcana.modID + "." + template_id + ".ingredients", "Relics");
            translationBuilder.add("item." + AscendantArcana.modID + "." + template_id + ".title", "Infusion");
            translationBuilder.add("item." + AscendantArcana.modID + "." + template_id + ".base_slot_description", "Add Gear");
            translationBuilder.add("item." + AscendantArcana.modID + "." + template_id + ".additions_slot_description", "Add Relic");

            translationBuilder.add(AArcanaItems.ASCENDANT_RELIC, "Ascendant Relic");
            translationBuilder.add(AArcanaItems.AWAKENED_RELIC, "Awakened Relic");
            translationBuilder.add(AArcanaItems.WAKING_RELIC, "Waking Relic");
            translationBuilder.add(AArcanaItems.STIRRING_RELIC, "Stirring Relic");
            translationBuilder.add(AArcanaItems.DORMANT_RELIC, "Dormant Relic");

            translationBuilder.add(AArcanaItems.ENCHANTED_SCRAP, "Enchanted Scrap");
            translationBuilder.add(AArcanaItems.RESTORINE, "Restorine");
        }
    }
}
