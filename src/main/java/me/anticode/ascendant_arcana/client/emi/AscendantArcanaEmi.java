package me.anticode.ascendant_arcana.client.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.client.emi.recipes.EmiEnchantmentRecipe;
import me.anticode.ascendant_arcana.client.emi.recipes.EmiInfusionRecipe;
import me.anticode.ascendant_arcana.client.emi.recipes.EmiRestorineRepairRecipe;
import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import me.anticode.ascendant_arcana.item.RelicItem;
import me.anticode.ascendant_arcana.logic.Relics;
import me.anticode.ascendant_arcana.recipe.EnchantmentRecipe;
import me.anticode.ascendant_arcana.recipe.InfusionRecipe;
import me.anticode.ascendant_arcana.recipe.RelicCraftingRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class AscendantArcanaEmi implements EmiPlugin {
    public static final Identifier EMI_SPRITES = new Identifier(AscendantArcana.modID, "textures/gui/emi_elements.png");
    public static final EmiStack ENCHANTING_TABLE = EmiStack.of(Blocks.ENCHANTING_TABLE);
    public static final EmiStack COPPER_ENCHANTING_TABLE = EmiStack.of(AArcanaBlocks.COPPER_ENCHANTING_TABLE);
    public static final EmiRecipeCategory ENCHANTING = new EmiRecipeCategory(new Identifier(AscendantArcana.modID, "enchanting"), COPPER_ENCHANTING_TABLE, new EmiTexture(EMI_SPRITES, 0, 0, 16, 16));

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addCategory(ENCHANTING);
        emiRegistry.addWorkstation(ENCHANTING, COPPER_ENCHANTING_TABLE);
        emiRegistry.addWorkstation(ENCHANTING, ENCHANTING_TABLE);

        RecipeManager manager = emiRegistry.getRecipeManager();
        for (EnchantmentRecipe recipe : manager.listAllOfType(AArcanaRecipes.ENCHANTMENT_RECIPE_TYPE)) {
            emiRegistry.addRecipe(new EmiEnchantmentRecipe(recipe));
        }
        for (CraftingRecipe recipe : manager.listAllOfType(RecipeType.CRAFTING)) {
            if (recipe instanceof RelicCraftingRecipe relicRecipe) {
                emiRegistry.addRecipe(new EmiCraftingRecipe(relicRecipe.getIngredients().stream().map(EmiIngredient::of).toList(), EmiStack.of(relicRecipe.getOutput()), relicRecipe.getId(), true));
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
            if (emiRegistry.isStackDisabled(EmiStack.of(item))) continue;
            if (item.getMaxDamage() > 0) {
                emiRegistry.addRecipe(new EmiRestorineRepairRecipe(EmiStack.of(item), new Identifier(AscendantArcana.modID, "/repair/").withSuffixedPath(Registries.ITEM.getId(item).getPath())));
            }
        }
    }
}
