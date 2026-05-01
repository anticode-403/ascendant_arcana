package me.anticode.ascendant_arcana.worldgen.feature;

import com.mojang.serialization.Codec;
import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.CaveSurface;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestorineGrowthFeature extends Feature<RestorineGrowthFeatureConfig> {
    public RestorineGrowthFeature(Codec<RestorineGrowthFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<RestorineGrowthFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        Random random = context.getRandom();
        RestorineGrowthFeatureConfig config = context.getConfig();

        Optional<CaveSurface> optional = CaveSurface.create(world, pos, config.floorToCeilingSearchRange(), RestorineGrowthFeature::canGenerate, RestorineGrowthFeature::canReplaceOrLava);
        if (optional.isPresent() && !(optional.get() instanceof CaveSurface.Empty)) {
            CaveSurface caveSurface = optional.get();
            int width = config.width().get(random);
            int heightScale = (int)(config.radiusToHeightRatio().get(random) * (float)width);
            if (caveSurface.getOptionalHeight().isPresent() && caveSurface.getOptionalHeight().getAsInt() < heightScale) return false;
            boolean isStalagmite = random.nextFloat() < config.ceilingPercentage();
            if (caveSurface instanceof CaveSurface.Half && caveSurface.getCeilingHeight().isPresent()) isStalagmite = true;
            else if (caveSurface instanceof CaveSurface.Half && caveSurface.getFloorHeight().isPresent()) isStalagmite = false;

            int y = isStalagmite ? caveSurface.getCeilingHeight().getAsInt() : caveSurface.getFloorHeight().getAsInt();
            RestorineGrowthGenerator generator = new RestorineGrowthGenerator(pos.withY(y), isStalagmite, width, 0.1, heightScale);
            if (!generator.canGenerate(world)) return false;

            generator.generate(world, random);
        }
        return false;
    }

    private static boolean canGenerate(BlockState blockState) {
        return blockState.isAir();
    }

    private static boolean canReplaceOrLava(BlockState state) {
        return state.isOf(Blocks.DRIPSTONE_BLOCK) || state.isIn(BlockTags.DRIPSTONE_REPLACEABLE_BLOCKS) || state.isOf(Blocks.LAVA);
    }

    static final class RestorineGrowthGenerator {
        private BlockPos pos;
        private final boolean isStalagmite;
        private int scale;
        private final double bluntness;
        private final double heightScale;

        RestorineGrowthGenerator(BlockPos pos, boolean isStalagmite, int scale, double bluntness, double heightScale) {
            this.pos = pos;
            this.isStalagmite = isStalagmite;
            this.scale = scale;
            this.bluntness = bluntness;
            this.heightScale = heightScale;
        }

        private int getBaseScale() {
            return this.scale(0.0F);
        }

        boolean canGenerate(StructureWorldAccess world) {
            while(this.scale > 1) {
                BlockPos.Mutable mutable = this.pos.mutableCopy();
                int i = Math.min(10, this.getBaseScale());

                for(int j = 0; j < i; ++j) {
                    if (world.getBlockState(mutable).isOf(Blocks.LAVA)) {
                        return false;
                    }

                    if (canGenerateBase(world, mutable, this.scale)) {
                        this.pos = mutable;
                        return true;
                    }

                    mutable.move(this.isStalagmite ? Direction.DOWN : Direction.UP);
                }

                this.scale /= 2;
            }

            return false;
        }

        static boolean canGenerateBase(StructureWorldAccess world, BlockPos pos, int height) {
            if (canGenerateOrLava(world, pos)) {
                return false;
            } else {
                float g = 6.0F / (float)height;

                for(float h = 0.0F; h < ((float)Math.PI * 2F); h += g) {
                    int i = (int)(MathHelper.cos(h) * (float)height);
                    int j = (int)(MathHelper.sin(h) * (float)height);
                    if (canGenerateOrLava(world, pos.add(i, 0, j))) {
                        return false;
                    }
                }

                return true;
            }
        }

        static boolean canGenerateOrLava(WorldAccess world, BlockPos pos) {
            return world.testBlockState(pos, RestorineGrowthGenerator::canGenerateOrLava);
        }

        public static boolean canGenerateOrLava(BlockState state) {
            return state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA);
        }

        private int scale(float height) {
            return (int)scaleHeightFromRadius(height, this.scale, this.heightScale, this.bluntness);
        }

        static double scaleHeightFromRadius(double radius, double scale, double heightScale, double bluntness) {
            if (radius < bluntness) {
                radius = bluntness;
            }

            double e = radius / scale * 0.384;
            double f = (double)0.75F * Math.pow(e, 1.3333333333333333);
            double g = Math.pow(e, 0.6666666666666666);
            double h = 0.3333333333333333 * Math.log(e);
            double i = heightScale * (f - g - h);
            i = Math.max(i, 0.0F);
            return i / 0.384 * scale;
        }

        void generate(StructureWorldAccess world, Random random) {
            List<BlockPos> budding_restorine = new ArrayList<>();
            for(int i = -this.scale; i <= this.scale; ++i) {
                for(int j = -this.scale; j <= this.scale; ++j) {
                    float f = MathHelper.sqrt((float)(i * i + j * j));
                    if (!(f > (float)this.scale)) {
                        int k = this.scale(f);
                        if (k > 0) {
                            if ((double)random.nextFloat() < 0.2) {
                                k = (int)((float)k * MathHelper.nextBetween(random, 0.8F, 1.0F));
                            }

                            BlockPos.Mutable mutable = this.pos.add(i, 0, j).mutableCopy();
                            boolean bl = false;
                            int l = this.isStalagmite ? world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, mutable.getX(), mutable.getZ()) : Integer.MAX_VALUE;

                            for(int m = 0; m < k && mutable.getY() < l; ++m) {
                                if (canGenerateOrLava(world, mutable)) {
                                    bl = true;
                                    Block block = Blocks.STONE;
                                    if (random.nextFloat() < 0.2F) {
                                        block = AArcanaBlocks.BUDDING_RESTORINE;
                                        budding_restorine.add(mutable.mutableCopy());
                                    }
                                    world.setBlockState(mutable, block.getDefaultState(), 2);
                                } else if (bl && world.getBlockState(mutable).isIn(BlockTags.BASE_STONE_OVERWORLD)) {
                                    break;
                                }

                                mutable.move(this.isStalagmite ? Direction.UP : Direction.DOWN);
                            }
                        }
                    }
                }
            }
            for (BlockPos pos : budding_restorine) {
                for (int l = 0; l < 6; l++) {
                    Block block = switch (random.nextBetween(0, 13)) {
                        case 0 -> AArcanaBlocks.SMALL_RESTORINE_BUD;
                        case 1 -> AArcanaBlocks.MEDIUM_RESTORINE_BUD;
                        case 2 -> AArcanaBlocks.LARGE_RESTORINE_BUD;
                        case 3, 4, 5, 6 -> AArcanaBlocks.RESTORINE_CLUSTER;
                        case 7, 8, 9, 10 -> AArcanaBlocks.MASSIVE_RESTORINE_CLUSTER;
                        default -> Blocks.AIR;
                    };
                    if (block == Blocks.AIR) continue;
                    BlockPos.Mutable mutable = pos.mutableCopy();
                    Direction[] directions = Direction.values();
                    BlockPos restorineBud = mutable.mutableCopy().add(directions[l].getVector());
                    if (world.getBlockState(restorineBud).isAir()) {
                        world.setBlockState(restorineBud, block.getDefaultState().with(AmethystClusterBlock.FACING, directions[l]).with(AmethystClusterBlock.WATERLOGGED, world.getBlockState(restorineBud).getFluidState().getFluid() == Fluids.WATER), 2);
                    }
                }
            }
        }
    }
}
