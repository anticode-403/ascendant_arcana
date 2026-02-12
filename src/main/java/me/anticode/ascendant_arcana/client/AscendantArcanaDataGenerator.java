package me.anticode.ascendant_arcana.client;

import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaTags;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.*;

import java.util.concurrent.CompletableFuture;

public class AscendantArcanaDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(AATagProvider::new);
    }

//    public static class AAModelProvider extends FabricModelProvider {
//
//    }

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
}
