package me.anticode.ascendant_arcana.client;

import com.google.common.collect.Lists;
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
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
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
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
                    .add(AArcanaItems.WARDEN_HEART);
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
                    .add(AArcanaBlocks.BUDDING_RESTORINE)
                    .add(AArcanaBlocks.COPPER_ENCHANTING_TABLE);
            getOrCreateTagBuilder(BlockTags.ENCHANTMENT_POWER_PROVIDER)
                    .add(Blocks.CHISELED_BOOKSHELF);
        }
    }

    public static class AAModelProvider extends FabricModelProvider {
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

            blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(AArcanaBlocks.CRYSTALIZED_LAVAL_BLOCK).coordinate(BlockStateVariantMap.create(Properties.AGE_3).register(0, BlockStateVariant.create().put(VariantSettings.MODEL, blockStateModelGenerator.createSubModel(AArcanaBlocks.CRYSTALIZED_LAVAL_BLOCK, "_0", Models.CUBE_ALL, TextureMap::all))).register(1, BlockStateVariant.create().put(VariantSettings.MODEL, blockStateModelGenerator.createSubModel(AArcanaBlocks.CRYSTALIZED_LAVAL_BLOCK, "_1", Models.CUBE_ALL, TextureMap::all))).register(2, BlockStateVariant.create().put(VariantSettings.MODEL, blockStateModelGenerator.createSubModel(AArcanaBlocks.CRYSTALIZED_LAVAL_BLOCK, "_2", Models.CUBE_ALL, TextureMap::all))).register(3, BlockStateVariant.create().put(VariantSettings.MODEL, blockStateModelGenerator.createSubModel(AArcanaBlocks.CRYSTALIZED_LAVAL_BLOCK, "_3", Models.CUBE_ALL, TextureMap::all)))));
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            itemModelGenerator.register(AArcanaItems.INFUSION_SMITHING_TEMPLATE, Models.GENERATED);

            itemModelGenerator.register(AArcanaItems.ENCHANTED_SCRAP, Models.GENERATED);
            itemModelGenerator.register(AArcanaItems.RESTORINE, Models.GENERATED);
            itemModelGenerator.register(AArcanaItems.WARDEN_HEART, Models.GENERATED);

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
            description(translationBuilder, enchantment, description);
        }

        private void registerStatusEffect(TranslationBuilder translationBuilder, StatusEffect statusEffect, String name, String description) {
            translationBuilder.add(statusEffect, name);
            translationBuilder.add(statusEffect.getTranslationKey() + ".description", description);
        }

        public <T> void registerTag(TranslationBuilder translationBuilder, TagKey<T> tag, String description) {
            translationBuilder.add("tag." + tag.id().toTranslationKey(), description);
        }

        private void description(TranslationBuilder translationBuilder, Item item, String description) {
            translationBuilder.add(item.getTranslationKey() + ".description", description);
        }

        private void description(TranslationBuilder translationBuilder, Enchantment enchantment, String description) {
            translationBuilder.add(enchantment.getTranslationKey() + ".description", description);
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
            description(translationBuilder, AArcanaItems.ENCHANTED_SCRAP, "An item made from the scrap of enchanted items and materials, used to make more enchantments.");
            translationBuilder.add(AArcanaItems.RESTORINE, "Restorine");
            description(translationBuilder, AArcanaItems.RESTORINE, "An item that acts as the universal repair ingredient.");
            translationBuilder.add(AArcanaItems.WARDEN_HEART, "Warden Heart");
            description(translationBuilder, AArcanaItems.WARDEN_HEART, "One of the Warden's many hearts, salvaged in whole and useful for enchanting.");
            // Relics
            translationBuilder.add(AArcanaItems.RELIC, "%1$s Relic of %2$s");
            description(translationBuilder, AArcanaItems.RELIC, "An item that can be infused into tools or armor at a Smithing Table, increasing their potential.");
            translationBuilder.add("item.relics.empty", "Empty Relic");
            translationBuilder.add("item.relics.unknown", "Unknown Relic");

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
            translationBuilder.add(AArcanaBlocks.SMALL_RESTORINE_BUD, "Small Restorine Bud");
            translationBuilder.add(AArcanaBlocks.MEDIUM_RESTORINE_BUD, "Medium Restorine Bud");
            translationBuilder.add(AArcanaBlocks.LARGE_RESTORINE_BUD, "Large Restorine Bud");
            translationBuilder.add(AArcanaBlocks.RESTORINE_CLUSTER, "Restorine Cluster");
            translationBuilder.add(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER, "Massive Restorine Cluster");
            // Attributes
            translationBuilder.add(AArcanaAttributes.PROTECTION, "Protection");
            translationBuilder.add(AArcanaAttributes.DAMAGE_TAKEN, "Damage Taken");
            // Enchantments
            registerEnchantment(translationBuilder, AArcanaEnchantments.ARCHERS_GAMBIT, "Archer's Gambit", "Briefly increased draw speed after consecutively hitting a target. Stacks 3 times.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.ALCHEMISTS_HEART, "Alchemist's Heart", "Increases the amplifier of all beneficial status effects.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.AMBUSH, "Ambush", "When you hit a mob after throwing this Trident, teleport to it.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.BLADEHEART, "Bladeheart", "Slightly increases all damage dealt by physical attacks.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.COLDHEART, "Coldheart", "Increases damage dealt by all cold attacks.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.CLEANSE, "Cleanse", "Holding the shield up cleanses you of all status effects after a short time.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.CROSS_COUNTER, "Cross Counter", "Blocking an attack immediately after raising the shield grants a brief damage bonus for the next attack.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.DEBILITATING_CHAIN, "Debilitating Chain", "Slaying a mob transfers all status effects to the nearest enemy.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.DEFLECT, "Deflect", "Blocking a projectile with your shield will shoot it back.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.EVOKERS_WRATH, "Evoker's Wrath", "Summons an Evoker Fang when the arrow lands.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.HELLWALKER, "Hellwalker", "Crystalizes nearby lava so it can be walked on.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.HOBBLING_SHOT, "Hobbling Shot", "Reduces movement speed and jump height, stacking 5 times.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.LIFETIDE, "Lifetide", "On hit, sticks into the target and heals them for a short duration. You heal half as much.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.NETHER_HEART, "Heart of the Nether", "Increases damage dealt by all fire attacks.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.PINCUSHION, "Pincushion", "Reduced base damage, increases damage dealt based on the number of arrows stuck in the target.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.PROTECTIVE_ECHO, "Protective Echo", "Instances of high damage are spread out over time.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.REJUVENATING_SHOT, "Rejuvenating Shot", "Instead of doing damage, arrows heal for half the damage they would have done.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.RICOCHET, "Ricochet", "Arrows ricochet, dealing reduced initial damage but increasing with each bounce.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.SMELTING, "Smelting", "Smelts blocks mined.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.SONIC_BLAST, "Sonic Blast", "Holding up the shield charges a powerful sonic blast that ignores most forms of protection.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.SOUL_BURST, "Soul Burst", "Slain enemies deal damage to nearby entities based on their maximum health.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.STORM_HEART, "Heart of the Storm", "Increases the damage dealt by all lightning attacks.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.STRAFE, "Strafe", "Allows you to sprint in any direction and reduces movement speed penalties while using an item.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.SUNDERING, "Sundering", "On hit, sticks into the target and deals damage over time, reducing their armor.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.SUREFOOT, "Surefoot", "Significantly increases knockback resistance and reduces the strength of most slowing effects.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.TURTLE_HEART, "Heart of the Turtle", "Decreases all incoming and outgoing damage.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.WITCH_HEART, "Witch's Heart", "Slightly increases damage dealt by all magic attacks.");

            registerEnchantment(translationBuilder, AArcanaEnchantments.DEPTHS_CURSE, "Curse of the Depths", "Drags you to the bottom of a body of water.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.ENFEEBLEMENT_CURSE, "Curse of Enfeeblement", "Reduces your maximum health.");
            registerEnchantment(translationBuilder, AArcanaEnchantments.INACCURACY_CURSE, "Curse of Inaccuracy", "Reduces the accuracy of bows and crossbows.");
            // Tooltips
            translationBuilder.add("item.book_contains_treasure_title", "Treasure Enchantment");
            translationBuilder.add("item.book_contains_treasure_body", "Store in Chiseled Bookshelves to unlock!");
            translationBuilder.add("item.enchantment_capacity", "Enchantment Capacity: %1$s/%2$s");
            // Enchantment UI stuff
            translationBuilder.add("gui.enchanting.level", "Level %1$s/%2$s");
            translationBuilder.add("gui.enchanting.max_level", "MAX LEVEL");
            translationBuilder.add("gui.enchanting.max_capacity", "This item cannot support more enchantments!");
            translationBuilder.add("gui.enchanting.low_level", "This Enchanting Table does not have enough power to decrypt this enchantment.");
            translationBuilder.add("gui.enchanting.treasure", "This enchantment is too exotic to decrypt without a copy nearby.");
            translationBuilder.add("gui.enchanting.no_item_title", "Ascendant Arcana");
            translationBuilder.add("gui.enchanting.no_item_body", "Enchanting has been completely overhauled. Each item has an enchantment capacity which cannot be exceeded and each enchantment requires various ingredients.\n\nTreasure enchantments can be unlocked by placing them in a nearby Chiseled Bookshelf.");
            translationBuilder.add("gui.enchanting.no_selection_body", "Select an enchantment and place the ingredients below. Capacity can be increased with relics.");
            translationBuilder.add("gui.enchanting.item_cost", "x%1$s %2$s");
            translationBuilder.add("gui.enchanting.enchant", "Enchant!");
            // Status Effects
            registerStatusEffect(translationBuilder, AArcanaStatusEffects.ARCHERS_GAMBIT, "Archer's Gambit", "Faster draw speed of bows and crossbows.");
            registerStatusEffect(translationBuilder, AArcanaStatusEffects.ECHOING_DAMAGE, "Echoing Damage", "Deals damage every second based on amplification.");
            registerStatusEffect(translationBuilder, AArcanaStatusEffects.HOBBLED, "Hobbled", "Slightly reduces movement speed and jump height.");
            registerStatusEffect(translationBuilder, AArcanaStatusEffects.SUNDERED, "Sundered", "Significantly reduces armor and armor toughness.");
            registerStatusEffect(translationBuilder, AArcanaStatusEffects.CROSS_COUNTER, "Cross Counter", "Increases attack damage for the next attack.");
            // Tags
            registerTag(translationBuilder, AArcanaTags.Items.HEARTS, "Warden Hearts");
            registerTag(translationBuilder, AArcanaTags.Items.RELICS, "Relics");
            registerTag(translationBuilder, AArcanaTags.Blocks.ENCHANTING_TABLES, "Enchanting Tables");
            // Emi
            translationBuilder.add("emi.category.ascendant_arcana.enchanting", "Enchanting");
            translationBuilder.add("gui.emi.ascendant_arcana.enchanting_power", "Required Enchanting Table power");
            translationBuilder.add("gui.emi.ascendant_arcana.level_cost", "XP Level Cost");
            translationBuilder.add("gui.emi.ascendant_arcana.capacity_cost", "Required Enchantment Capacity");
        }
    }

    public static class AARecipeProvider extends FabricRecipeProvider {
        public AARecipeProvider(FabricDataOutput output) {
            super(output);
        }

        public static class EnchantmentRecipeProvider implements RecipeJsonProvider {
            private final Identifier id;
            private final Enchantment enchantment;
            private int magicalScrapCost;
            private IngredientStack primaryIngredient;
            private IngredientStack secondaryIngredient;
            private int levelCost;

            EnchantmentRecipeProvider(Enchantment enchantment) {
                Identifier enchantmentId = Registries.ENCHANTMENT.getId(enchantment);
                assert enchantmentId != null;
                this.id = enchantmentId.withPrefixedPath("enchantments/");
                this.enchantment = enchantment;
            }

            EnchantmentRecipeProvider(Enchantment enchantment, int magicalScrapCost, IngredientStack primaryIngredient, IngredientStack secondaryIngredient, int levelCost) {
                Identifier enchantmentId = Registries.ENCHANTMENT.getId(enchantment);
                assert enchantmentId != null;
                this.id = enchantmentId.withPrefixedPath("enchantments/");
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
                Identifier enchantmentIdentifier = Registries.ENCHANTMENT.getId(enchantment);
                if (enchantmentIdentifier == null) return;
                String enchantmentId = enchantmentIdentifier.toString();

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
        
        public static class RelicRecipeJsonBuilder extends RecipeJsonBuilder implements CraftingRecipeJsonBuilder {
            private final RecipeCategory category;
            private final int strength;
            private final int relic;
            private final List<Ingredient> inputs = Lists.newArrayList();
            private final Advancement.Builder advancementBuilder = Advancement.Builder.createUntelemetered();
            @Nullable
            private String group;

            public RelicRecipeJsonBuilder(RecipeCategory category, int strength, int relic) {
                this.category = category;
                this.strength = strength;
                this.relic = relic;
            }

            public static RelicRecipeJsonBuilder create(RecipeCategory category, int strength, int relic) {
                return new RelicRecipeJsonBuilder(category, strength, relic);
            }

            public RelicRecipeJsonBuilder input(TagKey<Item> tag) {
                return this.input(Ingredient.fromTag(tag));
            }

            public RelicRecipeJsonBuilder input(ItemConvertible itemProvider) {
                return this.input(itemProvider, 1);
            }

            public RelicRecipeJsonBuilder input(ItemConvertible itemProvider, int size) {
                for(int i = 0; i < size; ++i) {
                    this.input(Ingredient.ofItems(itemProvider));
                }

                return this;
            }

            public RelicRecipeJsonBuilder input(Ingredient ingredient) {
                return this.input(ingredient, 1);
            }

            public RelicRecipeJsonBuilder input(Ingredient ingredient, int size) {
                for(int i = 0; i < size; ++i) {
                    this.inputs.add(ingredient);
                }

                return this;
            }

            public RelicRecipeJsonBuilder criterion(String string, CriterionConditions criterionConditions) {
                this.advancementBuilder.criterion(string, criterionConditions);
                return this;
            }

            public RelicRecipeJsonBuilder group(@Nullable String string) {
                this.group = string;
                return this;
            }

            @Override
            public Item getOutputItem() {
                return AArcanaItems.RELIC;
            }

            public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
                this.validate(recipeId);
                this.advancementBuilder.parent(ROOT).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(net.minecraft.advancement.AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
                exporter.accept(new RelicRecipeJsonBuilder.RelicRecipeJsonProvider(recipeId, this.strength, this.relic, this.group == null ? "" : this.group, getCraftingCategory(this.category), this.inputs, this.advancementBuilder, recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
            }

            private void validate(Identifier recipeId) {
                if (this.advancementBuilder.getCriteria().isEmpty()) {
                    throw new IllegalStateException("No way of obtaining recipe " + recipeId);
                }
            }

            public static class RelicRecipeJsonProvider extends RecipeJsonBuilder.CraftingRecipeJsonProvider {
                private final Identifier recipeId;
                private final int strength;
                private final int relic;
                private final String group;
                private final List<Ingredient> inputs;
                private final Advancement.Builder advancementBuilder;
                private final Identifier advancementId;

                public RelicRecipeJsonProvider(Identifier recipeId, int strength, int relic, String group, CraftingRecipeCategory craftingCategory, List<Ingredient> inputs, Advancement.Builder advancementBuilder, Identifier advancementId) {
                    super(craftingCategory);
                    this.recipeId = new Identifier(AscendantArcana.modID, recipeId.getPath() + "_" + strength + "_" + relic);
                    this.strength = strength;
                    this.relic = relic;
                    this.group = group;
                    this.inputs = inputs;
                    this.advancementBuilder = advancementBuilder;
                    this.advancementId = advancementId;
                }

                public void serialize(JsonObject json) {
                    super.serialize(json);
                    if (!this.group.isEmpty()) {
                        json.addProperty("group", this.group);
                    }

                    JsonArray jsonArray = new JsonArray();

                    for(Ingredient ingredient : this.inputs) {
                        jsonArray.add(ingredient.toJson());
                    }

                    json.add("ingredients", jsonArray);
                    json.addProperty("strength", this.strength);
                    json.addProperty("relic", this.relic);
                }

                public RecipeSerializer<?> getSerializer() {
                    return AArcanaRecipes.RELIC_CRAFTING_RECIPE_SERIALIZER;
                }

                public Identifier getRecipeId() {
                    return this.recipeId;
                }

                @Nullable
                public JsonObject toAdvancementJson() {
                    return this.advancementBuilder.toJson();
                }

                @Nullable
                public Identifier getAdvancementId() {
                    return this.advancementId;
                }
            }
        }


        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
            ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, AArcanaItems.ENCHANTED_SCRAP)
                    .input(Items.LAPIS_LAZULI, 2)
                    .input(Items.GOLD_NUGGET, 5)
                    .input(Items.AMETHYST_SHARD, 2)
                    .criterion("obtain_lapis", InventoryChangedCriterion.Conditions.items(Items.LAPIS_LAZULI))
                    .offerTo(exporter);

            RelicRecipeJsonBuilder.create(RecipeCategory.MISC, 1, 0)
                    .input(Items.AMETHYST_SHARD)
                    .input(Items.IRON_INGOT)
                    .criterion("obtain_amethyst", InventoryChangedCriterion.Conditions.items(Items.AMETHYST_SHARD))
                    .offerTo(exporter);
            RelicRecipeJsonBuilder.create(RecipeCategory.MISC, 1, 1)
                    .input(AArcanaItems.RESTORINE)
                    .input(Items.AMETHYST_SHARD)
                    .criterion("obtain_amethyst", InventoryChangedCriterion.Conditions.items(Items.AMETHYST_SHARD))
                    .offerTo(exporter);
            RelicRecipeJsonBuilder.create(RecipeCategory.MISC, 1, 2)
                    .input(Items.AMETHYST_SHARD)
                    .input(Items.CLAY_BALL, 2)
                    .criterion("obtain_amethyst", InventoryChangedCriterion.Conditions.items(Items.AMETHYST_SHARD))
                    .offerTo(exporter);
            RelicRecipeJsonBuilder.create(RecipeCategory.MISC, 1, 3)
                    .input(Items.AMETHYST_SHARD)
                    .input(Items.GOLD_NUGGET)
                    .criterion("obtain_amethyst", InventoryChangedCriterion.Conditions.items(Items.AMETHYST_SHARD))
                    .offerTo(exporter);
            RelicRecipeJsonBuilder.create(RecipeCategory.MISC, 1, 4)
                    .input(Items.LAPIS_LAZULI)
                    .input(Items.AMETHYST_SHARD)
                    .criterion("obtain_amethyst", InventoryChangedCriterion.Conditions.items(Items.AMETHYST_SHARD))
                    .offerTo(exporter);

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, AArcanaBlocks.COPPER_ENCHANTING_TABLE)
                    .input('b', Items.BOOK)
                    .input('c', Items.CUT_COPPER)
                    .input('g', AArcanaItems.RESTORINE)
                    .pattern(" b ").pattern("gcg").pattern("ccc")
                    .criterion("obtain_copper", InventoryChangedCriterion.Conditions.items(Items.COPPER_INGOT))
                    .offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, AArcanaItems.INFUSION_SMITHING_TEMPLATE)
                    .input('c', Blocks.CALCITE)
                    .input('r', AArcanaItems.RESTORINE)
                    .pattern(" c ")
                    .pattern("rrr")
                    .pattern("ccc")
                    .criterion("obtain_restorine", InventoryChangedCriterion.Conditions.items(AArcanaItems.RESTORINE))
                    .offerTo(exporter);

            // Enchantments
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.ARCHERS_GAMBIT).primary(Items.GOLD_INGOT, 3).level(7));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.ALCHEMISTS_HEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.GLISTERING_MELON_SLICE, 16).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.AMBUSH).primary(Items.ENDER_PEARL, 8).secondary(Items.AMETHYST_SHARD, 3).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.BLADEHEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.DIAMOND, 8).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.COLDHEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.BLUE_ICE, 32).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.CLEANSE).scrap(9).primary(Items.MILK_BUCKET, 1).secondary(Items.PITCHER_PLANT, 4).level(9));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.CROSS_COUNTER).scrap(3).primary(Items.CLOCK, 1).secondary(Items.IRON_NUGGET, 1).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.DEBILITATING_CHAIN).primary(Items.FERMENTED_SPIDER_EYE, 3).secondary(Items.GUNPOWDER, 3).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.DEFLECT).scrap(4).primary(Items.SLIME_BALL, 4).secondary(Items.SCUTE, 2).level(5));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.EVOKERS_WRATH).scrap(2).primary(Items.TOTEM_OF_UNDYING, 1).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.HELLWALKER).scrap(12).primary(Items.BLAZE_ROD, 2).secondary(Items.TORCHFLOWER, 1).level(6));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.HOBBLING_SHOT).scrap(3).primary(Items.VINE, 6).secondary(Items.BONE_MEAL, 6).level(6));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.LIFETIDE).scrap(12).primary(Items.GHAST_TEAR, 6).level(9));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.NETHER_HEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.NETHERITE_INGOT, 2).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.PINCUSHION).scrap(3).primary(Items.ECHO_SHARD, 1).secondary(Items.IRON_INGOT, 6).level(6));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.PROTECTIVE_ECHO).scrap(6).primary(Items.POPPED_CHORUS_FRUIT, 4).secondary(Items.SCUTE, 2).level(8));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.REJUVENATING_SHOT).scrap(6).primary(Items.GHAST_TEAR, 4).level(6));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.RICOCHET).scrap(6).primary(Items.SLIME_BALL, 23).level(6));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.SMELTING).primary(Items.BLAZE_ROD, 2).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.SONIC_BLAST).scrap(6).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.ECHO_SHARD, 6).level(9));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.SOUL_BURST).primary(Items.SCULK_CATALYST, 1).secondary(Items.GUNPOWDER, 12).level(3));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.STORM_HEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.LIGHTNING_ROD, 6).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.STRAFE).scrap(6).primary(Items.FEATHER, 12).level(4));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.SUNDERING).scrap(4).primary(Items.TORCHFLOWER, 2).secondary(Items.FERMENTED_SPIDER_EYE, 4).level(6));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.SUREFOOT).scrap(6).primary(Items.ANVIL, 1).level(6));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.TURTLE_HEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.ANVIL, 1).level(15));
            exporter.accept(new EnchantmentRecipeProvider(AArcanaEnchantments.WITCH_HEART).scrap(12).primary(AArcanaTags.Items.HEARTS, 1).secondary(Items.CAULDRON, 1).level(15));

            exporter.accept(new EnchantmentRecipeProvider(Enchantments.AQUA_AFFINITY).scrap(3).primary(Items.PRISMARINE_CRYSTALS, 4).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.CHANNELING).scrap(5).primary(Items.LIGHTNING_ROD, 1).secondary(Items.GOLD_INGOT, 3).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.DEPTH_STRIDER).scrap(6).primary(Items.SCUTE, 4).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.FIRE_ASPECT).scrap(6).primary(Items.BLAZE_POWDER, 9).secondary(Items.FLINT, 2).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.FLAME).scrap(6).primary(Items.BLAZE_POWDER, 12).secondary(Items.FLINT, 2).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.FORTUNE).scrap(12).primary(Items.DIAMOND, 3).secondary(Items.ECHO_SHARD, 1).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.FROST_WALKER).scrap(6).primary(Items.BLUE_ICE, 2).secondary(Items.ECHO_SHARD, 1).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.FEATHER_FALLING).scrap(2).primary(Items.FEATHER, 5).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.INFINITY).scrap(12).primary(Items.ECHO_SHARD, 2).secondary(Items.ARROW, 1).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.THORNS).scrap(3).primary(Items.CACTUS, 2).secondary(Items.SLIME_BALL, 1).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.KNOCKBACK).scrap(1).primary(Items.PISTON, 2).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.SOUL_SPEED).scrap(6).primary(Items.SCULK_CATALYST, 1).secondary(Items.FEATHER, 2).level(6));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.SWIFT_SNEAK).scrap(6).primary(Items.FEATHER, 5).secondary(Items.ECHO_SHARD, 1).level(6));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.LOOTING).scrap(12).primary(Items.ENDER_PEARL, 3).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.RESPIRATION).scrap(3).primary(Items.GLASS_BOTTLE, 2).secondary(Items.BAMBOO, 1).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.RIPTIDE).scrap(9).primary(Items.NAUTILUS_SHELL, 2).secondary(Items.PRISMARINE_CRYSTALS, 3).level(9));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.LOYALTY).scrap(4).primary(Items.COPPER_INGOT, 6).secondary(Items.SALMON, 1).level(5));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.SWEEPING).scrap(2).primary(Items.AMETHYST_SHARD, 2).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.PIERCING).scrap(2).primary(Items.STONE, 3).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.SILK_TOUCH).scrap(4).primary(Items.STRING, 6).level(5));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.PUNCH).scrap(2).primary(Items.PISTON, 2).secondary(Items.REDSTONE, 4).level(4));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.MULTISHOT).scrap(6).primary(Items.ECHO_SHARD, 2).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.LURE).scrap(1).primary(Items.IRON_NUGGET, 1).secondary(Items.COD, 3).level(3));
            exporter.accept(new EnchantmentRecipeProvider(Enchantments.LUCK_OF_THE_SEA).scrap(4).primary(Items.RABBIT_FOOT, 1).level(3));
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
            addDrop(AArcanaBlocks.RESTORINE_CLUSTER, LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(AArcanaItems.RESTORINE)).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2)))));
            dropsWithSilkTouch(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER);
            addDrop(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER, LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(AArcanaItems.RESTORINE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3, 6))))));
            addDrop(AArcanaBlocks.COPPER_ENCHANTING_TABLE);
        }
    }
}
