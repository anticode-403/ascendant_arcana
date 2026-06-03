package me.anticode.ascendant_arcana.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class CrystalizedLavaBlock extends FrostedIceBlock {

    public CrystalizedLavaBlock(Settings settings) {
        super(settings);
    }

    public static BlockState getLavaState() {
        return Blocks.LAVA.getDefaultState();
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        world.setBlockState(pos, getLavaState(), 0);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if ((random.nextInt(5) == 0 && canMelt(world, pos, 4)) && increaseAge(state, world, pos)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for(Direction direction : Direction.values()) {
                mutable.set(pos, direction);
                BlockState blockState = world.getBlockState(mutable);
                if (blockState.isOf(this) && !increaseAge(blockState, world, mutable)) {
                    world.scheduleBlockTick(mutable, this, MathHelper.nextInt(random, 20, 40));
                }
            }

        } else {
            world.scheduleBlockTick(pos, this, MathHelper.nextInt(random, 20, 40));
        }
    }

    @Override
    protected void melt(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, getLavaState());
        world.updateNeighbor(pos, getLavaState().getBlock(), pos);
    }

    private boolean increaseAge(BlockState state, World world, BlockPos pos) {
        int i = state.get(AGE);
        if (i < MAX_AGE) {
            world.setBlockState(pos, state.with(AGE, i + 1), 2);
            return false;
        } else {
            this.melt(state, world, pos);
            return true;
        }
    }

    private boolean canMelt(BlockView world, BlockPos pos, int maxNeighbors) {
        int i = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(Direction direction : Direction.values()) {
            mutable.set(pos, direction);
            if (world.getBlockState(mutable).isOf(this)) {
                ++i;
                if (i >= maxNeighbors) {
                    return false;
                }
            }
        }

        return true;
    }
}
