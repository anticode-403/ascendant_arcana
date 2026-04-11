package me.anticode.ascendant_arcana;

import me.anticode.ascendant_arcana.api.ItemEntryAccess;
import me.anticode.ascendant_arcana.api.LeafEntryAccess;
import me.anticode.ascendant_arcana.config.AArcanaServerConfig;
import me.anticode.ascendant_arcana.config.AArcanaServerConfigWrapper;
import me.anticode.ascendant_arcana.init.*;
import me.anticode.ascendant_arcana.loot.PopulateRelicLootFunction;
import me.anticode.ascendant_arcana.networking.EnchantingScreenSendRecipe;
import me.anticode.ascendant_arcana.screenhandler.AArcanaEnchantingScreenHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AscendantArcana implements ModInitializer {

    public static String modID = "ascendant_arcana";
    public static final Logger LOGGER = LoggerFactory.getLogger(modID);
    public static AArcanaServerConfig config;

    @Override
    public void onInitialize() {
        AArcanaItems.initialize();
        AArcanaTags.initialize();
        AArcanaBlocks.initialize();
        AArcanaRecipes.initialize();
        AArcanaAttributes.initialize();
        AArcanaEnchantments.initialize();
        AArcanaStatusEffects.initialize();
        AArcanaScreenHandlers.initialize();
        AArcanaLootFunctionTypes.initialize();

        ServerPlayNetworking.registerGlobalReceiver(EnchantingScreenSendRecipe.Id, (server, player, handler, buf, responseSender) -> {
            EnchantingScreenSendRecipe packet = EnchantingScreenSendRecipe.read(buf, player.getWorld().getRecipeManager());
            if (player.currentScreenHandler.syncId != packet.syncId()) return;
            AArcanaEnchantingScreenHandler screenHandler = (AArcanaEnchantingScreenHandler) player.currentScreenHandler;
            screenHandler.recipe = packet.recipe();
        });

        AutoConfig.register(AArcanaServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        config = AutoConfig.getConfigHolder(AArcanaServerConfigWrapper.class).getConfig().server;

        LootTableEvents.MODIFY.register(((resourceManager, lootManager, identifier, builder, lootTableSource) -> {
            if (lootTableSource.isBuiltin()) {
                if (identifier.equals(Identifier.of("minecraft", "entities/warden"))) {
                    LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry.builder(AArcanaItems.WARDEN_HEART));
                    builder.pool(poolBuilder.build());
                }
            }
            if (identifier.getPath().contains("chests")) {
                LOGGER.debug(identifier.getPath());
                builder.modifyPools((poolBuilder) -> {
                    LootPool pool = poolBuilder.build();
                    for (LootPoolEntry entry : pool.entries) {
                        if (entry instanceof ItemEntry) {
                            LOGGER.debug("true");
                            LOGGER.debug(((ItemEntryAccess)entry).ascendantArcana$getItem().getTranslationKey());
                            if (((ItemEntryAccess)entry).ascendantArcana$getItem() == Items.BOOK) {
                                boolean enchanted = false;
                                for (LootFunction function : ((LeafEntryAccess)entry).ascendantArcana$getFunctions()) {
                                    if (function instanceof EnchantRandomlyLootFunction || function instanceof EnchantWithLevelsLootFunction) {
                                        enchanted = true;
                                        break;
                                    }
                                }
                                if (!enchanted) continue;
                                LOGGER.debug("BASED!!!");
                                LeafEntry.Builder<?> entryBuilder = ItemEntry.builder(AArcanaItems.RELIC);
                                entryBuilder.weight(((LeafEntryAccess) entry).ascendantArcana$getWeight());
                                entryBuilder.quality(((LeafEntryAccess) entry).ascendantArcana$getQuality());
                                entryBuilder.apply(PopulateRelicLootFunction.builder(UniformLootNumberProvider.create(1, 3), new int[]{0, 1, 2, 3, 4}));
                                // I can't figure out how to replicate conditions, so in the off change the enchanted book has a conditional drop, we will unfortunately ignore it
                                poolBuilder.with(entryBuilder.build());
                            }
                        }
                    }
                });
            }
        }));
    }
}
