package me.anticode.ascendant_arcana.client;

import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class AscendantArcanaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.SMALL_RESTORINE_BUD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.MEDIUM_RESTORINE_BUD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.LARGE_RESTORINE_BUD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.RESTORINE_CLUSTER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER, RenderLayer.getCutout());
    }
}
