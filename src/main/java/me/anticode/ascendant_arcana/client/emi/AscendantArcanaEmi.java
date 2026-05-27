package me.anticode.ascendant_arcana.client.emi;

import com.google.common.collect.Lists;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.client.emi.recipes.*;
import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import me.anticode.ascendant_arcana.item.RelicItem;
import me.anticode.ascendant_arcana.logic.Relics;
import me.anticode.ascendant_arcana.recipe.EnchantmentRecipe;
import me.anticode.ascendant_arcana.recipe.InfusionRecipe;
import me.anticode.ascendant_arcana.recipe.RelicCraftingRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class AscendantArcanaEmi implements EmiPlugin {
    public static final Identifier EMI_SPRITES = new Identifier(AscendantArcana.modID, "textures/gui/emi_elements.png");
    public static final EmiStack ENCHANTING_TABLE = EmiStack.of(Blocks.ENCHANTING_TABLE);
    public static final EmiStack COPPER_ENCHANTING_TABLE = EmiStack.of(AArcanaBlocks.COPPER_ENCHANTING_TABLE);
    public static final EmiRecipeCategory ENCHANTING = new EmiRecipeCategory(new Identifier(AscendantArcana.modID, "enchanting"), COPPER_ENCHANTING_TABLE, new EmiTexture(EMI_SPRITES, 0, 0, 16, 16));

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.setDefaultComparison(AArcanaItems.RELIC, Comparison.compareNbt());

        emiRegistry.addCategory(ENCHANTING);
        emiRegistry.addWorkstation(ENCHANTING, COPPER_ENCHANTING_TABLE);
        emiRegistry.addWorkstation(ENCHANTING, ENCHANTING_TABLE);

        RecipeManager manager = emiRegistry.getRecipeManager();
        for (EnchantmentRecipe recipe : manager.listAllOfType(AArcanaRecipes.ENCHANTMENT_RECIPE_TYPE)) {
            emiRegistry.addRecipe(new EmiEnchantmentRecipe(recipe));
        }
        for (CraftingRecipe recipe : manager.listAllOfType(RecipeType.CRAFTING)) {
            if (recipe instanceof RelicCraftingRecipe relicRecipe) {
                List<EmiIngredient> ingredients = relicRecipe.getIngredients().stream().map((ingredient) -> {
                    if (ingredient.test(new ItemStack(AArcanaItems.RELIC))) {
                        ItemStack stack = relicRecipe.getOutput().copy();
                        RelicItem.writeRelicData(stack, RelicItem.getRelicType(stack), RelicItem.getRelicStrength(stack) - 1);
                        return EmiStack.of(stack);
                    }
                    else return EmiIngredient.of(ingredient);
                }).toList();
                emiRegistry.addRecipe(new EmiCraftingRecipe(ingredients, EmiStack.of(relicRecipe.getOutput()), relicRecipe.getId(), true));
            }
        }
        for (SmithingRecipe recipe : manager.listAllOfType(RecipeType.SMITHING)) {
            if (recipe instanceof InfusionRecipe infusionRecipe) {
                for (int i = 0; i < Relics.values().length; i++) {
                    Relics relicType = Relics.fromId(i);
                    ItemStack stack = new ItemStack(AArcanaItems.RELIC);
                    RelicItem.writeRelicData(stack, relicType, 1);
                    emiRegistry.addRecipe(new EmiInfusionRecipe(infusionRecipe, stack));
                }
            }
        }
        for (Item item : Registries.ITEM) {
            List<Enchantment> targetedEnchantments = Lists.newArrayList();
            List<Enchantment> universalEnchantments = Lists.newArrayList();
            for (Enchantment enchantment : Registries.ENCHANTMENT.stream().toList()) {
                try {
                    if (enchantment.isAcceptableItem(ItemStack.EMPTY)) {
                        universalEnchantments.add(enchantment);
                        continue;
                    }
                } catch (Throwable ignored) {
                }
                targetedEnchantments.add(enchantment);
            }
            if (emiRegistry.isStackDisabled(EmiStack.of(item))) continue;
            if (item.getMaxDamage() > 0) {
                emiRegistry.addRecipe(new EmiRestorineRepairRecipe(EmiStack.of(item), new Identifier(AscendantArcana.modID, "/repair/").withSuffixedPath(Registries.ITEM.getId(item).getPath())));
            }

            ItemStack defaultStack = item.getDefaultStack();
            int acceptableEnchantments = 0;
            for (Enchantment e : targetedEnchantments) {
                if (e.isAcceptableItem(defaultStack) && defaultStack.isEnchantable()
                        && defaultStack.getItem().isEnchantable(defaultStack)) {
                    acceptableEnchantments++;
                }
            }
            if (acceptableEnchantments > 0) {
                for (Enchantment e : universalEnchantments) {
                    if (e.isAcceptableItem(defaultStack)) {
                        acceptableEnchantments++;
                    }
                }
                emiRegistry.addRecipe(new EmiGrindstoneScrapRecipe(item, new Identifier(AscendantArcana.modID, "/grindstone/scrap/").withSuffixedPath(Registries.ITEM.getId(item).getPath())));
            }
        }

        emiRegistry.removeRecipes((t) -> {
            Identifier id = t.getId();
            if (id == null) return false;
            return id.getPath().contains("grindstone/disenchanting");
        });

        for (Enchantment e : Registries.ENCHANTMENT.stream().toList()) {
            if (!e.isCursed()) {
                int max = Math.min(10, e.getMaxLevel());
                int min = e.getMinLevel();
                while (min <= max) {
                    int level = min;
                    min++;
                }
            }
        }
    }
}
