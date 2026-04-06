package me.anticode.ascendant_arcana;

import me.anticode.ascendant_arcana.config.AArcanaServerConfig;
import me.anticode.ascendant_arcana.config.AArcanaServerConfigWrapper;
import me.anticode.ascendant_arcana.init.*;
import me.anticode.ascendant_arcana.networking.EnchantingScreenSendRecipe;
import me.anticode.ascendant_arcana.screenhandler.AArcanaEnchantingScreenHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
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
        }));
    }
}
