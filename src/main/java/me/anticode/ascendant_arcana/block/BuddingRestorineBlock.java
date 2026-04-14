package me.anticode.ascendant_arcana.block;

import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class BuddingRestorineBlock extends Block {
    public static final int GROW_CHANCE = 5;
    private static final Direction[] DIRECTIONS = Direction.values();

    public BuddingRestorineBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(GROW_CHANCE) == 0) {
            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            Block block = null;
            if (canGrowIn(blockState)) {
                block = AArcanaBlocks.SMALL_RESTORINE_BUD;
            } else if (blockState.isOf(AArcanaBlocks.SMALL_RESTORINE_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = AArcanaBlocks.MEDIUM_RESTORINE_BUD;
            } else if (blockState.isOf(AArcanaBlocks.MEDIUM_RESTORINE_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = AArcanaBlocks.LARGE_RESTORINE_BUD;
            } else if (blockState.isOf(AArcanaBlocks.LARGE_RESTORINE_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = AArcanaBlocks.RESTORINE_CLUSTER;
            }

            if (block != null) {
                BlockState blockState2 = block.getDefaultState().with(AmethystClusterBlock.FACING, direction).with(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
                world.setBlockState(blockPos, blockState2);
            }

        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    public static boolean canGrowIn(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER) && state.getFluidState().getLevel() == 8;
    }
}
