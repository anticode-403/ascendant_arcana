package me.anticode.ascendant_arcana.enchantment;

import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HellWalker extends Enchantment {
    public HellWalker() {
        super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        return source.isIn(DamageTypeTags.IS_FIRE) ? 12 : 0;
    }

    public int getMinPower(int level) {
        return 20;
    }

    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    public boolean isTreasure() {
        return true;
    }

    public static void freezeLava(LivingEntity entity, World world, BlockPos blockPos) {
        if (entity.isOnGround()) {
            BlockState blockState = AArcanaBlocks.CRYSTALIZED_LAVAL_BLOCK.getDefaultState();
            int i = Math.min(16, 4);
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-i, -1, -i), blockPos.add(i, -1, i))) {
                if (blockPos2.isWithinDistance(entity.getPos(), i)) {
                    mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                    BlockState blockState2 = world.getBlockState(mutable);
                    if (blockState2.isAir()) {
                        BlockState blockState3 = world.getBlockState(blockPos2);
                        if (blockState3.getBlock() == Blocks.LAVA && blockState3.get(FluidBlock.LEVEL) == 0 && blockState.canPlaceAt(world, blockPos2) && world.canPlace(blockState, blockPos2, ShapeContext.absent())) {
                            world.setBlockState(blockPos2, blockState);
                            world.scheduleBlockTick(blockPos2, AArcanaBlocks.CRYSTALIZED_LAVAL_BLOCK, MathHelper.nextInt(entity.getRandom(), 60, 120));
                        }
                    }
                }
            }

        }
    }

    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.DEPTH_STRIDER && other != Enchantments.FROST_WALKER;
    }
}
