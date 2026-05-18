package me.anticode.ascendant_arcana.block;

import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.util.math.BlockPos;

public class CopperEnchantingTableBlockEntity extends EnchantingTableBlockEntity {

    public CopperEnchantingTableBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return AArcanaBlocks.COPPER_ENCHANTING_TABLE_BLOCK_ENTITY;
    }
}
