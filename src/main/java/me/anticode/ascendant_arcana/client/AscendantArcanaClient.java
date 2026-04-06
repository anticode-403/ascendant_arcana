package me.anticode.ascendant_arcana.client;

import me.anticode.ascendant_arcana.client.screen.AArcanaEnchantingScreen;
import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaScreenHandlers;
import me.anticode.ascendant_arcana.item.RelicItem;
import me.anticode.ascendant_arcana.logic.Relics;
import me.anticode.ascendant_arcana.networking.EnchantingScreenSync;
import me.anticode.ascendant_arcana.screenhandler.AArcanaEnchantingScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.util.Identifier;

public class AscendantArcanaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(EnchantingScreenSync.Id, (client, handler, buf, responseSender) -> {
            EnchantingScreenSync packet = EnchantingScreenSync.read(buf);
            if (client.player == null) return;
            if (client.player.currentScreenHandler.syncId != packet.syncId()) return;
            AArcanaEnchantingScreenHandler screenHandler = (AArcanaEnchantingScreenHandler) client.player.currentScreenHandler;
            screenHandler.unlockedTreasures = packet.treasures();
        });

        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.SMALL_RESTORINE_BUD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.MEDIUM_RESTORINE_BUD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.LARGE_RESTORINE_BUD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.RESTORINE_CLUSTER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER, RenderLayer.getCutout());

        ModelPredicateProviderRegistry.register(AArcanaItems.RELIC, new Identifier("relic_type"), (itemStack, clientWorld, livingEntity, seed) -> Relics.toId(RelicItem.getRelicType(itemStack)) / 5F);
        ModelPredicateProviderRegistry.register(AArcanaItems.RELIC, new Identifier("relic_strength"), (itemStack, clientWorld, livingEntity, seed) -> RelicItem.getRelicStrength(itemStack) / 5F);

        BlockEntityRendererRegistryImpl.register(AArcanaBlocks.COPPER_ENCHANTING_TABLE_BLOCK_ENTITY, EnchantingTableBlockEntityRenderer::new);
        HandledScreens.register(AArcanaScreenHandlers.ENCHANTING, AArcanaEnchantingScreen::new);
    }
}
