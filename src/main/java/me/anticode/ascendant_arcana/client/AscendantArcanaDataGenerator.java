package me.anticode.ascendant_arcana.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.init.*;
import me.anticode.ascendant_arcana.logic.Relics;
import me.anticode.ascendant_arcana.recipe.IngredientStack;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

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
            getOrCreateTagBuilder(AArcanaTags.Items.HEARTS)
                    .add(Items.SCULK_CATALYST);
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
            getOrCreateTagBuilder(BlockTags.ENCHANTMENT_POWER_PROVIDER)
                    .add(Blocks.CHISELED_BOOKSHELF);
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

        private void registerEnchantment(TranslationBuilder translationBuilder, Enchantment enchantment, String name, String description) {
            translationBuilder.add(enchantment, name);
            translationBuilder.add(enchantment.getTranslationKey() + ".description", description);
        }

        private void registerStatusEffect(TranslationBuilder translationBuilder, StatusEffect statusEffect, String name, String description) {
            translationBuilder.add(statusEffect, name);
            translationBuilder.add(statusEffect.getTranslationKey() + ".description", description);
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
            translationBuilder.add(AArcanaBlocks.COPPER_ENCHANTING_TABLE, "Copper Enchanting Table");
            // Restorine Clusters
            translationBuilder.add(AArcanaBlocks.SMALL_RESTORINE_BUD, "Small Restore Bud");
            translationBuilder.add(AArcanaBlocks.MEDIUM_RESTORINE_BUD, "Medium Restore Bud");
            translationBuilder.add(AArcanaBlocks.LARGE_RESTORINE_BUD, "Large Restore Bud");
            translationBuilder.add(AArcanaBlocks.RESTORINE_CLUSTER, "Restore Cluster");
            translationBuilder.add(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER, "Massive Restore Cluster");
            // Attributes
            translationBuilder.add(AArcanaAttributes.PROTECTION, "Protection");
            translationBuilder.add(AArcanaAttributes.DAMAGE_TAKEN, "Damage Taken");
            // Enchantments
            registerEnchantment(translationBuilder, AArcanaEnchantments.ARCHERS_GAMBIT, "Archer's Gambit", "Briefly increased draw speed after consecutively hitting a target. Stacks 3 times.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.ALCHEMISTS_HEART, "Alchemist's Heart", "Increases the amplifier of all beneficial status effects.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.AMBUSH, "Ambush", "When you hit a mob after throwing this Trident, teleport to it.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.DEBILITATING_CHAIN, "Debilitating Chain", "Slaying a mob transfers all status effects to the nearest enemy.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.BLADEHEART, "Bladeheart", "Slightly increases all damage dealt by physical attacks.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.COLDHEART, "Coldheart", "Increases damage dealt by all cold attacks.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.EVOKERS_WRATH, "Evoker's Wrath", "Summons an Evoker Fang when the arrow lands.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.NETHER_HEART, "Heart of the Nether", "Increases damage dealt by all fire attacks.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.REJUVENATING_SHOT, "Rejuvenating Shot", "Instead of doing damage, arrows heal for half the damage they would have done.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.RICOCHET, "Ricochet", "Arrows ricochet but deal reduced damage that massively increases after each bounce.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.SMELTING, "Smelting", "Smelts blocks mined.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.SOUL_BURST, "Soul Burst", "Slain enemies deal damage to nearby entities based on their maximum health.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.STORM_HEART, "Heart of the Storm", "Increases the damage dealt by all lightning attacks.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.STRAFE, "Strafe", "Allows you to sprint in any direction and reduces movement speed penalties while using an item.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.TURTLE_HEART, "Heart of the Turtle", "Decreases all incoming and outgoing damage.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.WITCH_HEART, "Witch's Heart", "Slightly increases damage dealt by all magic attacks.");

            registerEnchantment(translationBuilder, AArcanaEnchantments.INACCURACY_CURSE, "Curse of Inaccuracy", "Reduces the accuracy of bows and crossbows.");
            // Enchantment Capacity Tooltip
            translationBuilder.add("item.enchantment_capacity", "Enchantment Capacity: %1$s/%2$s");
            // Enchantment UI stuff
            translationBuilder.add("gui.enchanting.level", "Level %1$s/%2$s");
            translationBuilder.add("gui.enchanting.max_level", "MAX LEVEL");
            translationBuilder.add("gui.enchanting.max_capacity", "This item cannot support more enchantments!");
            translationBuilder.add("gui.enchanting.low_level", "This enchanting table does not have enough power to decrypt this enchantment.");
            // Status Effects
            registerStatusEffect(translationBuilder, AArcanaStatusEffects.ARCHERS_GAMBIT, "Archer's Gambit", "Faster draw speed of bows and crossbows.");
        }
    }

    public static class AARecipeProvider extends FabricRecipeProvider {
        public AARecipeProvider(FabricDataOutput output) {
            super(output);
        }

        public static class EnchantmentRecipeProvider implements RecipeJsonProvider {
            private Identifier id;
            private Enchantment enchantment;
            private int magicalScrapCost;
            private IngredientStack primaryIngredient;
            private IngredientStack secondaryIngredient;
            private int levelCost;

            EnchantmentRecipeProvider(Enchantment enchantment) {
                this.id = Registries.ENCHANTMENT.getId(enchantment).withPrefixedPath("enchantments/");
                this.enchantment = enchantment;
            }

            EnchantmentRecipeProvider(Enchantment enchantment, int magicalScrapCost, IngredientStack primaryIngredient, IngredientStack secondaryIngredient, int levelCost) {
                this.id = Registries.ENCHANTMENT.getId(enchantment).withPrefixedPath("enchantments/");
                this.enchantment = enchantment;
                this.magicalScrapCost = magicalScrapCost;
                this.primaryIngredient = primaryIngredient;
                this.secondaryIngredient = secondaryIngredient;
                this.levelCost = levelCost;
            }

            public EnchantmentRecipeProvider scrap(int count) {
                this.magicalScrapCost = count;
                return this;
            }

            public EnchantmentRecipeProvider primary(ItemConvertible itemProvider, int count) {
                this.primaryIngredient = new IngredientStack(Ingredient.ofItems(itemProvider), count);
                return this;
            }

            public EnchantmentRecipeProvider primary(TagKey<Item> items, int count) {
                this.primaryIngredient = new IngredientStack(Ingredient.fromTag(items), count);
                return this;
            }

            public EnchantmentRecipeProvider secondary(ItemConvertible itemProvider, int count) {
                this.secondaryIngredient = new IngredientStack(Ingredient.ofItems(itemProvider), count);
                return this;
            }

            public EnchantmentRecipeProvider secondary(TagKey<Item> items, int count) {
                this.secondaryIngredient = new IngredientStack(Ingredient.fromTag(items), count);
                return this;
            }

            public EnchantmentRecipeProvider level(int levels) {
                this.levelCost = levels;
                return this;
            }

            @Override
            public void serialize(JsonObject json) {
                String enchantmentId = Registries.ENCHANTMENT.getId(enchantment).toString();

                if (magicalScrapCost != 0) json.addProperty("magical_scrap_cost", magicalScrapCost);
                else  json.addProperty("magical_scrap_cost", 3);
                if (primaryIngredient != null) json.add("primary_ingredient", primaryIngredient.toJson());
                if (secondaryIngredient != null) json.add("secondary_ingredient", secondaryIngredient.toJson());
                if (levelCost != 0) json.addProperty("level_cost", levelCost);
                json.addProperty("enchantment", enchantmentId);
            }

            @Override
            public Identifier getRecipeId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getSerializer() {
                return AArcanaRecipes.ENCHANTMENT_RECIPE_SERIALIZER;
            }

            @Override
            public @Nullable JsonObject toAdvancementJson() {
                return null;
            }

            @Override
            public @Nullable Identifier getAdvancementId() {
                return null;
            }
        }

        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
            ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, AArcanaItems.ENCHANTED_SCRAP, 1)
                    .input(Items.LAPIS_LAZULI, 2)
                    .input(Items.GOLD_NUGGET, 5)
                    .input(Items.AMETHYST_SHARD, 2)
                    .criterion("obtain_lapis", InventoryChangedCriterion.Conditions.items(Items.LAPIS_LAZULI))
                    .offerTo(exporter);

            // Enchantments
            // Replace Sculk Catalysts with some form of Warden Heart
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.ARCHERS_GAMBIT).primary(Items.GOLD_INGOT, 3).level(7));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.ALCHEMISTS_HEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.GLISTERING_MELON_SLICE, 16).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.AMBUSH).primary(Items.ENDER_PEARL, 8).secondary(Items.AMETHYST_SHARD, 3).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.DEBILITATING_CHAIN).primary(Items.FERMENTED_SPIDER_EYE, 3).secondary(Items.GUNPOWDER, 3).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.BLADEHEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.DIAMOND, 8).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.COLDHEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.BLUE_ICE, 32).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.EVOKERS_WRATH).scrap(2).primary(Items.TOTEM_OF_UNDYING, 1).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.NETHER_HEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.NETHERITE_INGOT, 2).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.REJUVENATING_SHOT).scrap(6).primary(Items.GHAST_TEAR, 4).level(6));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.RICOCHET).scrap(6).primary(Items.SLIME_BALL, 23).level(6));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.SMELTING).primary(Items.BLAZE_ROD, 2).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.SOUL_BURST).primary(Items.SCULK_CATALYST, 1).secondary(Items.GUNPOWDER, 12).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.STORM_HEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.LIGHTNING_ROD, 6).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.STRAFE).scrap(6).primary(Items.FEATHER, 12).level(4));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.TURTLE_HEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.ANVIL, 1).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.WITCH_HEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.CAULDRON, 1).level(15));

            // TODO: Vanilla Minecraft enchantments
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
