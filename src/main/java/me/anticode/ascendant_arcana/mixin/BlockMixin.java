package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(
            method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void smeltingMod(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity,
                                    Entity entity, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (EnchantmentHelper.getLevel(AArcanaEnchantments.SMELTING, stack) > 0) {
            List<ItemStack> drops = cir.getReturnValue();
            if (!drops.isEmpty()) {
                drops = new ArrayList<>(drops);
//                boolean smeltsSelf = state.isIn()
                int dropsSize = drops.size();
                for (int i = 0; i < dropsSize; i++) {
                    Pair<ItemStack, Float> smelted = getSmeltedStack(world, drops.get(i));
                    if (smelted != null) {
                        world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        drops.set(i, smelted.getLeft());
                        AbstractFurnaceBlockEntityAccessor.ascendant_arcana$dropExperience(world, entity.getPos(), 1, smelted.getRight());
                    }
                }
                cir.setReturnValue(drops);
            }
        }
    }

    @Unique
    private static Pair<ItemStack, Float> getSmeltedStack(ServerWorld world, ItemStack stack) {
        for (SmeltingRecipe recipe : world.getRecipeManager().listAllOfType(RecipeType.SMELTING)) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.test(stack)) {
                    return new Pair<>(new ItemStack(recipe.getOutput(world.getRegistryManager()).getItem(), recipe.getOutput(world.getRegistryManager()).getCount() * stack.getCount()), recipe.getExperience());
                }
            }
        }
        return null;
    }
}
