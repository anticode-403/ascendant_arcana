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
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
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
        initializeConfigIfNull();

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

        LootTableEvents.MODIFY.register(((resourceManager, lootManager, identifier, builder, lootTableSource) -> {
            if (lootTableSource.isBuiltin() && (config.add_boss_drops || config.add_relics_to_entities)) {
                if (identifier.equals(Identifier.of("minecraft", "entities/warden"))) {
                    if (config.add_boss_drops) {
                        LootPool.Builder heartPool = LootPool.builder().with(ItemEntry.builder(AArcanaItems.WARDEN_HEART));
                        builder.pool(heartPool.build());
                    }
                    if (config.add_relics_to_entities) {
                        LootPool.Builder relicPool = LootPool.builder().with(ItemEntry.builder(AArcanaItems.RELIC).apply(PopulateRelicLootFunction.builder(ConstantLootNumberProvider.create(5), new int[]{0,2,4})));
                        builder.pool(relicPool.build());
                    }
                }
                else if (config.add_relics_to_entities) {
                    if (identifier.equals(Identifier.of("minecraft", "entities/witch"))) {
                        LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry.builder(AArcanaItems.RELIC).apply(PopulateRelicLootFunction.builder(UniformLootNumberProvider.create(2, 4), new int[]{4})).weight(1)).with(EmptyEntry.builder().weight(19));
                        builder.pool(poolBuilder.build());
                    }
                    else if (identifier.equals(Identifier.of("minecraft", "entities/wither"))) {
                        LootPool.Builder relicPool = LootPool.builder().with(ItemEntry.builder(AArcanaItems.RELIC).apply(PopulateRelicLootFunction.builder(ConstantLootNumberProvider.create(5), new int[]{1,2,3})));
                        builder.pool(relicPool.build());
                    }
                    else if (identifier.equals(Identifier.of("minecraft", "entities/ender_dragon"))) {
                        LootPool.Builder relicPool = LootPool.builder().with(ItemEntry.builder(AArcanaItems.RELIC).apply(PopulateRelicLootFunction.builder(ConstantLootNumberProvider.create(5), new int[]{2,3})));
                        builder.pool(relicPool.build());
                    }
                    else if (identifier.equals(Identifier.of("minecraft", "entities/wither_skeleton"))) {
                        LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry.builder(AArcanaItems.RELIC).apply(PopulateRelicLootFunction.builder(UniformLootNumberProvider.create(2, 4), new int[]{0})).weight(1)).with(EmptyEntry.builder().weight(19));
                        builder.pool(poolBuilder.build());
                    }
                    else if (identifier.equals(Identifier.of("minecraft", "entities/evoker"))) {
                        LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry.builder(AArcanaItems.RELIC).apply(PopulateRelicLootFunction.builder(UniformLootNumberProvider.create(2, 4), new int[]{4})).weight(1)).with(EmptyEntry.builder().weight(19));
                        builder.pool(poolBuilder.build());
                    }
                    else if (identifier.equals(Identifier.of("minecraft", "gameplay/piglin_bartering"))) {
                        builder.modifyPools((poolBuilder) -> {
                            poolBuilder.with(ItemEntry.builder(AArcanaItems.RELIC).apply(PopulateRelicLootFunction.builder(UniformLootNumberProvider.create(2,4), new int[]{3})).weight(13));
                        });
                    }
                    else if (identifier.getPath().contains("archaeology/")) {
                        builder.modifyPools((poolBuilder) -> {
                            poolBuilder.with(ItemEntry.builder(AArcanaItems.RELIC).apply(PopulateRelicLootFunction.builder(UniformLootNumberProvider.create(2,4), new int[]{1})));
                        });
                    }
                }
            }
            if (identifier.getPath().contains("chests") && config.add_relics_to_chests || config.add_restorine_to_chests) {
                builder.modifyPools((poolBuilder) -> {
                    LootPool pool = poolBuilder.build();
                    for (LootPoolEntry entry : pool.entries) {
                        if (entry instanceof ItemEntry) {
                            if (((ItemEntryAccess)entry).ascendantArcana$getItem() == Items.BOOK && config.add_relics_to_chests) {
                                boolean enchanted = false;
                                boolean bonus = false;
                                for (LootFunction function : ((LeafEntryAccess)entry).ascendantArcana$getFunctions()) {
                                    if (function instanceof EnchantRandomlyLootFunction || function instanceof EnchantWithLevelsLootFunction) {
                                        enchanted = true;
                                        if (function instanceof EnchantRandomlyLootFunction) bonus = true;
                                        break;
                                    }
                                }
                                if (!enchanted) continue;
                                LeafEntry.Builder<?> entryBuilder = ItemEntry.builder(AArcanaItems.RELIC);
                                entryBuilder.weight(((LeafEntryAccess) entry).ascendantArcana$getWeight());
                                entryBuilder.quality(((LeafEntryAccess) entry).ascendantArcana$getQuality());
                                entryBuilder.apply(PopulateRelicLootFunction.builder(UniformLootNumberProvider.create(1, !bonus ? 3 : 4), new int[]{0, 1, 2, 4}));
                                // I can't figure out how to replicate conditions, so in the off chance the enchanted book has a conditional drop, we will unfortunately ignore it
                                poolBuilder.with(entryBuilder.build());
                            } else if ((((ItemEntryAccess)entry).ascendantArcana$getItem() == Items.AMETHYST_SHARD || ((ItemEntryAccess)entry).ascendantArcana$getItem() == Items.DIAMOND) && config.add_restorine_to_chests) {
                                LeafEntry.Builder<?> entryBuilder = ItemEntry.builder(AArcanaItems.RESTORINE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3)));
                                if (((ItemEntryAccess)entry).ascendantArcana$getItem() == Items.DIAMOND)
                                    entryBuilder.weight(((LeafEntryAccess) entry).ascendantArcana$getWeight() / 4);
                                else
                                    entryBuilder.weight(((LeafEntryAccess) entry).ascendantArcana$getWeight() / 2);
                                entryBuilder.quality(((LeafEntryAccess) entry).ascendantArcana$getQuality());
                                poolBuilder.with(entryBuilder.build());
                            }
                        }
                    }
                });
            }
        }));
    }

    public static void initializeConfigIfNull() {
        if (config != null) return;
        AutoConfig.register(AArcanaServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        config = AutoConfig.getConfigHolder(AArcanaServerConfigWrapper.class).getConfig().server;
    }
}
