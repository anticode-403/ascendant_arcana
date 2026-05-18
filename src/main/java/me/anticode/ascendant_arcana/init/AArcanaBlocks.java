package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.block.BuddingRestorineBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class AArcanaBlocks {
    public static final Block BUDDING_RESTORINE = register(
            new BuddingRestorineBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool().resistance(3f).hardness(4.5f)),
            "budding_restorine",
            true
    );

    public static final AmethystClusterBlock SMALL_RESTORINE_BUD = (AmethystClusterBlock) register(
            new AmethystClusterBlock(3, 4, AbstractBlock.Settings.create().solid().nonOpaque().ticksRandomly().sounds(BlockSoundGroup.AMETHYST_CLUSTER).requiresTool().pistonBehavior(PistonBehavior.DESTROY).strength(1.5F).luminance((state) -> 1)),
            "small_restorine_bud",
            true
    );
    public static final AmethystClusterBlock MEDIUM_RESTORINE_BUD = (AmethystClusterBlock) register(
            new AmethystClusterBlock(4, 3, AbstractBlock.Settings.copy(SMALL_RESTORINE_BUD).luminance((state) -> 2)),
            "medium_restorine_bud",
            true
    );
    public static final AmethystClusterBlock LARGE_RESTORINE_BUD = (AmethystClusterBlock) register(
            new AmethystClusterBlock(5, 3, AbstractBlock.Settings.copy(SMALL_RESTORINE_BUD).luminance((state) -> 3)),
            "large_restorine_bud",
            true
    );
    public static final AmethystClusterBlock RESTORINE_CLUSTER = (AmethystClusterBlock) register(
            new AmethystClusterBlock(7, 3, AbstractBlock.Settings.copy(SMALL_RESTORINE_BUD).luminance((state) -> 4)),
            "restorine_cluster",
            true
    );
    public static final AmethystClusterBlock MASSIVE_RESTORINE_CLUSTER = (AmethystClusterBlock) register(
            new AmethystClusterBlock(9, 2, AbstractBlock.Settings.copy(SMALL_RESTORINE_BUD).luminance((state) -> 5)),
            "massive_restorine_cluster",
            true
    );

    public static final EnchantingTableBlock COPPER_ENCHANTING_TABLE = (EnchantingTableBlock) register(
            new EnchantingTableBlock(AbstractBlock.Settings.copy(Blocks.ENCHANTING_TABLE).luminance((state) -> 0).strength(2.0F, 1200.0F).sounds(BlockSoundGroup.COPPER)),
            "copper_enchanting_table",
            true
    );
    public static final BlockEntityType<EnchantingTableBlockEntity> COPPER_ENCHANTING_TABLE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(AscendantArcana.modID, "copper_enchanting_table"),
            BlockEntityType.Builder.create(EnchantingTableBlockEntity::new, COPPER_ENCHANTING_TABLE).build(null)
    );

    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        Identifier id = new Identifier(AscendantArcana.modID, name);
        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }
        return Registry.register(Registries.BLOCK, id, block);
    }

    private static ItemStack after(Item item) {
        return new ItemStack(item);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register((itemGroup) -> {
            itemGroup.addAfter(after(Items.AMETHYST_CLUSTER), AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER);
            itemGroup.addAfter(after(Items.AMETHYST_CLUSTER), AArcanaBlocks.RESTORINE_CLUSTER);
            itemGroup.addAfter(after(Items.AMETHYST_CLUSTER), AArcanaBlocks.LARGE_RESTORINE_BUD);
            itemGroup.addAfter(after(Items.AMETHYST_CLUSTER), AArcanaBlocks.MEDIUM_RESTORINE_BUD);
            itemGroup.addAfter(after(Items.AMETHYST_CLUSTER), AArcanaBlocks.SMALL_RESTORINE_BUD);
            itemGroup.addAfter(after(Items.AMETHYST_CLUSTER), AArcanaBlocks.BUDDING_RESTORINE);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> {
            itemGroup.addBefore(after(Items.ENCHANTING_TABLE), AArcanaBlocks.COPPER_ENCHANTING_TABLE);
        });
    }
}
