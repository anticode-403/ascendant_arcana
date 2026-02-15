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
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AscendantArcanaDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(AATagProvider::new);
        pack.addProvider(AAModelProvider::new);
        pack.addProvider(AALanguageProvider::new);
        pack.addProvider(AARecipeProvider::new);
    }

    public static class AAModelProvider extends FabricModelProvider {
//        public static final Model RESTORINE_CROSS = new Model(
//
//        );

        public AAModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

            blockStateModelGenerator.registerCubeAllModelTexturePool(AArcanaBlocks.BUDDING_RESTORINE);

            // Restorine clusters
            blockStateModelGenerator.registerAmethyst(AArcanaBlocks.SMALL_RESTORINE_BUD);
            blockStateModelGenerator.registerParentedItemModel(AArcanaBlocks.SMALL_RESTORINE_BUD.asItem(), ModelIds.getBlockModelId(AArcanaBlocks.SMALL_RESTORINE_BUD));

            blockStateModelGenerator.registerAmethyst(AArcanaBlocks.MEDIUM_RESTORINE_BUD);
            blockStateModelGenerator.registerParentedItemModel(AArcanaBlocks.MEDIUM_RESTORINE_BUD.asItem(), ModelIds.getBlockModelId(AArcanaBlocks.MEDIUM_RESTORINE_BUD));

            blockStateModelGenerator.registerAmethyst(AArcanaBlocks.LARGE_RESTORINE_BUD);
            blockStateModelGenerator.registerParentedItemModel(AArcanaBlocks.LARGE_RESTORINE_BUD.asItem(), ModelIds.getBlockModelId(AArcanaBlocks.LARGE_RESTORINE_BUD));

            blockStateModelGenerator.registerAmethyst(AArcanaBlocks.RESTORINE_CLUSTER);
            blockStateModelGenerator.registerParentedItemModel(AArcanaBlocks.RESTORINE_CLUSTER.asItem(), ModelIds.getBlockModelId(AArcanaBlocks.RESTORINE_CLUSTER));

            blockStateModelGenerator.registerAmethyst(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER);
            blockStateModelGenerator.registerParentedItemModel(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER.asItem(), ModelIds.getBlockModelId(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER));
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
            // Smithing Templates
            String template_id = AArcanaItems.INFUSION_SMITHING_TEMPLATE_ID;
            translationBuilder.add("item." + AscendantArcana.modID + "." + template_id + ".applies_to", "Armor, Tools, and Weapons");
            translationBuilder.add("item." + AscendantArcana.modID + "." + template_id + ".ingredients", "Relics");
            translationBuilder.add("item." + AscendantArcana.modID + "." + template_id + ".title", "Infusion");
            translationBuilder.add("item." + AscendantArcana.modID + "." + template_id + ".base_slot_description", "Add Gear");
            translationBuilder.add("item." + AscendantArcana.modID + "." + template_id + ".additions_slot_description", "Add Relic");
            // Items
            translationBuilder.add(AArcanaItems.ENCHANTED_SCRAP, "Enchanted Scrap");
            translationBuilder.add(AArcanaItems.RESTORINE, "Restorine");
            // Relics
            translationBuilder.add(AArcanaItems.ASCENDANT_RELIC, "Ascendant Relic");
            translationBuilder.add(AArcanaItems.AWAKENED_RELIC, "Awakened Relic");
            translationBuilder.add(AArcanaItems.WAKING_RELIC, "Waking Relic");
            translationBuilder.add(AArcanaItems.STIRRING_RELIC, "Stirring Relic");
            translationBuilder.add(AArcanaItems.DORMANT_RELIC, "Dormant Relic");
            // Blocks
            translationBuilder.add(AArcanaBlocks.BUDDING_RESTORINE, "Budding Restorine");
            // Restorine Clusters
            translationBuilder.add(AArcanaBlocks.SMALL_RESTORINE_BUD, "Small Restore Bud");
            translationBuilder.add(AArcanaBlocks.MEDIUM_RESTORINE_BUD, "Medium Restore Bud");
            translationBuilder.add(AArcanaBlocks.LARGE_RESTORINE_BUD, "Large Restore Bud");
            translationBuilder.add(AArcanaBlocks.RESTORINE_CLUSTER, "Restore Cluster");
            translationBuilder.add(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER, "Massive Restore Cluster");
        }
    }

    public static class AARecipeProvider extends FabricRecipeProvider {
        public AARecipeProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
            ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, AArcanaItems.ENCHANTED_SCRAP, 1)
                    .input(Items.LAPIS_LAZULI, 2)
                    .input(Items.GOLD_NUGGET, 5)
                    .input(Items.AMETHYST_SHARD, 2)
                    .criterion("obtain_lapis", InventoryChangedCriterion.Conditions.items(Items.LAPIS_LAZULI))
                    .offerTo(exporter);

        }
    }
}
