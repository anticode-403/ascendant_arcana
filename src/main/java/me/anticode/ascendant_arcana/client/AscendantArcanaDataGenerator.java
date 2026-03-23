package me.anticode.ascendant_arcana.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.init.AArcanaAttributes;
import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaTags;
import me.anticode.ascendant_arcana.logic.Relics;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AscendantArcanaDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(AAItemTagProvider::new);
        pack.addProvider(AABlockTagProvider::new);
        pack.addProvider(AAModelProvider::new);
        pack.addProvider(AALanguageProvider::new);
        pack.addProvider(AARecipeProvider::new);
        pack.addProvider(AABlockLootTableProvider::new);
    }

    public static class AAItemTagProvider extends FabricTagProvider<Item> {

        public AAItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.ITEM, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(AArcanaTags.Items.RELICS)
                    .add(AArcanaItems.RELIC);
        }
    }

    public static class AABlockTagProvider extends FabricTagProvider<Block> {

        public AABlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.BLOCK, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                    .add(AArcanaBlocks.SMALL_RESTORINE_BUD)
                    .add(AArcanaBlocks.MEDIUM_RESTORINE_BUD)
                    .add(AArcanaBlocks.LARGE_RESTORINE_BUD)
                    .add(AArcanaBlocks.RESTORINE_CLUSTER)
                    .add(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER)
                    .add(AArcanaBlocks.BUDDING_RESTORINE);
        }
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

            // Restorine Clusters
            blockStateModelGenerator.registerAmethyst(AArcanaBlocks.SMALL_RESTORINE_BUD);
            blockStateModelGenerator.registerItemModel(AArcanaBlocks.SMALL_RESTORINE_BUD);

            blockStateModelGenerator.registerAmethyst(AArcanaBlocks.MEDIUM_RESTORINE_BUD);
            blockStateModelGenerator.registerItemModel(AArcanaBlocks.MEDIUM_RESTORINE_BUD);

            blockStateModelGenerator.registerAmethyst(AArcanaBlocks.LARGE_RESTORINE_BUD);
            blockStateModelGenerator.registerItemModel(AArcanaBlocks.LARGE_RESTORINE_BUD);

            blockStateModelGenerator.registerAmethyst(AArcanaBlocks.RESTORINE_CLUSTER);
            blockStateModelGenerator.registerItemModel(AArcanaBlocks.RESTORINE_CLUSTER);

            blockStateModelGenerator.registerAmethyst(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER);
            blockStateModelGenerator.registerItemModel(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            itemModelGenerator.register(AArcanaItems.INFUSION_SMITHING_TEMPLATE, Models.GENERATED);

            itemModelGenerator.register(AArcanaItems.ENCHANTED_SCRAP, Models.GENERATED);
            itemModelGenerator.register(AArcanaItems.RESTORINE, Models.GENERATED);

            // Relics
            // We use model predicates here to change the relic texture on the fly while only using one item.
            // Minecraft doesn't provide us a way to do this easily, so we have to build the JSON file manually.
            JsonObject rootJsonObject = new JsonObject();
            rootJsonObject.addProperty("parent", "minecraft:item/generated");
            JsonObject textureJsonObject = new JsonObject();
            textureJsonObject.addProperty("layer0", "ascendant_arcana:item/relic_magic_1");
            rootJsonObject.add("textures", textureJsonObject);
            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < Relics.values().length * 5; i++) {
                int relicId = MathHelper.floor((double) i / 5);
                int strength = i + 1 - (relicId * 5);
                String relicName = switch(relicId) {
                    case 0 -> "damage";
                    case 1 -> "durability";
                    case 2 -> "protection";
                    case 3 -> "haste";
                    case 4 -> "magic";
                    default -> "error";
                };
                JsonObject overrideObject = new JsonObject();
                overrideObject.addProperty("model", AscendantArcana.modID + ":item/relics/relic_" + relicName + "_" + strength);
                JsonObject predicateObject = new JsonObject();
                predicateObject.addProperty("relic_type", relicId / 5F); // Model Predicates are clamped between 0 and 1 for some reason
                predicateObject.addProperty("relic_strength", strength / 5F);
                overrideObject.add("predicate", predicateObject);
                jsonArray.add(overrideObject);

                Models.GENERATED.upload(new Identifier(AscendantArcana.modID, "item/relics/relic_" + relicName + "_" + strength), TextureMap.layer0(new Identifier(AscendantArcana.modID, "item/relic_" + relicName + "_" + strength)), itemModelGenerator.writer);
            }
            rootJsonObject.add("overrides", jsonArray);
            Models.GENERATED.upload(ModelIds.getItemModelId(AArcanaItems.RELIC), TextureMap.layer0(AArcanaItems.RELIC), itemModelGenerator.writer, (id, textures) -> rootJsonObject);
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
            translationBuilder.add("item." + AscendantArcana.modID + ".smithing_template." + template_id + ".applies_to", "Armor, Tools, and Weapons");
            translationBuilder.add("item." + AscendantArcana.modID + ".smithing_template." + template_id + ".ingredients", "Relics");
            translationBuilder.add("item." + AscendantArcana.modID + ".smithing_template." + template_id + ".title", "Infusion");
            translationBuilder.add("item." + AscendantArcana.modID + ".smithing_template." + template_id + ".base_slot_description", "Add Gear");
            translationBuilder.add("item." + AscendantArcana.modID + ".smithing_template." + template_id + ".additions_slot_description", "Add Relic");
            // Items
            translationBuilder.add(AArcanaItems.ENCHANTED_SCRAP, "Enchanted Scrap");
            translationBuilder.add(AArcanaItems.RESTORINE, "Restorine");
            // Relics
            translationBuilder.add(AArcanaItems.RELIC, "%1$s Relic of %2$s");
            translationBuilder.add("item.relics.empty", "Empty Relic");

            translationBuilder.add("item.relics.strength.1", "Dormant");
            translationBuilder.add("item.relics.strength.2", "Stirring");
            translationBuilder.add("item.relics.strength.3", "Waking");
            translationBuilder.add("item.relics.strength.4", "Awakened");
            translationBuilder.add("item.relics.strength.5", "Ascendant");

            translationBuilder.add("item.relics.type.damage", "Damage");
            translationBuilder.add("item.relics.type.durability", "Durability");
            translationBuilder.add("item.relics.type.protection", "Protection");
            translationBuilder.add("item.relics.type.haste", "Swiftness");
            translationBuilder.add("item.relics.type.enchantment_capacity", "Enchantment Capacity");

            translationBuilder.add("item.relics.name.damage", "Violence");
            translationBuilder.add("item.relics.name.durability", "Immutability");
            translationBuilder.add("item.relics.name.protection", "Shielding");
            translationBuilder.add("item.relics.name.haste", "Haste");
            translationBuilder.add("item.relics.name.enchantment_capacity", "Magic");

            translationBuilder.add("item.relics.tooltip", "+%1$s%3$s %2$s");
            translationBuilder.add("item.relics.tooltip.applied_any", "When Applied to Item:");
            translationBuilder.add("item.relics.tooltip.applied_tool", "When Applied to Tool:");
            translationBuilder.add("item.relics.tooltip.applied_armor", "When Applied to Armor:");
            translationBuilder.add("item.relics.tooltip.on_tool", "Infused Relics (%1$s/%2$s):");
            // Blocks
            translationBuilder.add(AArcanaBlocks.BUDDING_RESTORINE, "Budding Restorine");
            // Restorine Clusters
            translationBuilder.add(AArcanaBlocks.SMALL_RESTORINE_BUD, "Small Restore Bud");
            translationBuilder.add(AArcanaBlocks.MEDIUM_RESTORINE_BUD, "Medium Restore Bud");
            translationBuilder.add(AArcanaBlocks.LARGE_RESTORINE_BUD, "Large Restore Bud");
            translationBuilder.add(AArcanaBlocks.RESTORINE_CLUSTER, "Restore Cluster");
            translationBuilder.add(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER, "Massive Restore Cluster");
            // Attributes
            translationBuilder.add(AArcanaAttributes.PROTECTION, "Protection");
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

    public static class AABlockLootTableProvider extends FabricBlockLootTableProvider {
        protected AABlockLootTableProvider(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            // Restorine Clusters
            dropsWithSilkTouch(AArcanaBlocks.SMALL_RESTORINE_BUD);
            dropsWithSilkTouch(AArcanaBlocks.MEDIUM_RESTORINE_BUD);
            dropsWithSilkTouch(AArcanaBlocks.LARGE_RESTORINE_BUD);
            dropsWithSilkTouch(AArcanaBlocks.RESTORINE_CLUSTER);
            addDrop(AArcanaBlocks.RESTORINE_CLUSTER, LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(AArcanaItems.RESTORINE))));
            dropsWithSilkTouch(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER);
            addDrop(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER, LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(AArcanaItems.RESTORINE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))))));
        }
    }
}
