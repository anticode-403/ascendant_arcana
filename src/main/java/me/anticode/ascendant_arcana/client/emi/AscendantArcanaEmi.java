package me.anticode.ascendant_arcana.client.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.client.emi.recipes.EmiEnchantmentRecipe;
import me.anticode.ascendant_arcana.init.AArcanaBlocks;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import me.anticode.ascendant_arcana.recipe.EnchantmentRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.recipe.RecipeManager;
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
    }
}
