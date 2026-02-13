package me.anticode.ascendant_arcana.client;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaTags;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.registry.*;

import java.util.concurrent.CompletableFuture;

public class AscendantArcanaDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(AATagProvider::new);
        pack.addProvider(AAModelProvider::new);
    }

    public static class AAModelProvider extends FabricModelProvider {
        public AAModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            itemModelGenerator.register(AArcanaItems.INFUSION_SMITHING_TEMPLATE, Models.GENERATED);
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
