package me.anticode.ascendant_arcana.client;

import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.item.RelicItem;
import me.anticode.ascendant_arcana.logic.Relics;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class AscendantArcanaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.SMALL_RESTORINE_BUD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.MEDIUM_RESTORINE_BUD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.LARGE_RESTORINE_BUD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.RESTORINE_CLUSTER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER, RenderLayer.getCutout());

        ModelPredicateProviderRegistry.register(AArcanaItems.RELIC, new Identifier("relic_type"), (itemStack, clientWorld, livingEntity, seed) -> Relics.toId(RelicItem.getRelicType(itemStack)) / 5F);
        ModelPredicateProviderRegistry.register(AArcanaItems.RELIC, new Identifier("relic_strength"), (itemStack, clientWorld, livingEntity, seed) -> RelicItem.getRelicStrength(itemStack) / 5F);
    }
}
