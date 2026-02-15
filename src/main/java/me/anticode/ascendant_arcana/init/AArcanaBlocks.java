package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class AArcanaBlocks {
    public static final Block BUDDING_RESTORINE = register(
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool().resistance(3f).hardness(4.5f)),
            "budding_restorine",
            true
    );

    public static final AmethystClusterBlock SMALL_RESTORINE_BUD = (AmethystClusterBlock) register(
            new AmethystClusterBlock(3, 4, AbstractBlock.Settings.create()),
            "small_restorine_bud",
            true
    );
    public static final AmethystClusterBlock MEDIUM_RESTORINE_BUD = (AmethystClusterBlock) register(
            new AmethystClusterBlock(4, 3, AbstractBlock.Settings.create()),
            "medium_restorine_bud",
            true
    );
    public static final AmethystClusterBlock LARGE_RESTORINE_BUD = (AmethystClusterBlock) register(
            new AmethystClusterBlock(5, 3, AbstractBlock.Settings.create()),
            "large_restorine_bud",
            true
    );
    public static final AmethystClusterBlock RESTORINE_CLUSTER = (AmethystClusterBlock) register(
            new AmethystClusterBlock(7, 3, AbstractBlock.Settings.create()),
            "restorine_cluster",
            true
    );
    public static final AmethystClusterBlock MASSIVE_RESTORINE_CLUSTER = (AmethystClusterBlock) register(
            new AmethystClusterBlock(9, 2, AbstractBlock.Settings.create()),
            "massive_restorine_cluster",
            true
    );

    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        Identifier id = new Identifier(AscendantArcana.modID, name);
        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void initialize() {}
}
