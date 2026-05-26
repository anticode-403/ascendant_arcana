package me.anticode.ascendant_arcana.client.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.anticode.ascendant_arcana.client.emi.AscendantArcanaPlugin;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.recipe.EnchantmentRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmiEnchantmentRecipe implements EmiRecipe {
    private final Identifier id;
    private final int levelCost;
    private final int scrapCost;
    private final EmiIngredient primaryStack;
    private final EmiIngredient secondaryStack;
    private final Enchantment output;

    public EmiEnchantmentRecipe(EnchantmentRecipe recipe) {
        this.id = recipe.getId();
        this.output = recipe.enchantment;
        this.levelCost = recipe.levelCost;
        this.scrapCost = recipe.magicalScrapCost;
        if (recipe.primaryIngredientStack != null) {
            this.primaryStack = EmiIngredient.of(recipe.primaryIngredientStack.getIngredient(), (long)recipe.primaryIngredientStack.getCount());
        } else primaryStack = null;
        if (recipe.secondaryIngredientStack != null) {
            this.secondaryStack = EmiIngredient.of(recipe.secondaryIngredientStack.getIngredient(), (long)recipe.secondaryIngredientStack.getCount());
        } else secondaryStack = null;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return AscendantArcanaPlugin.ENCHANTING;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(EmiStack.of(AArcanaItems.ENCHANTED_SCRAP, scrapCost));
        if (primaryStack != null) inputs.add(primaryStack);
        if (secondaryStack != null) inputs.add(secondaryStack);
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of();
    }

    @Override
    public int getDisplayWidth() {
        return 106;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        widgetHolder.addSlot(EmiStack.of(AArcanaItems.ENCHANTED_SCRAP, scrapCost), 0, 0);
        EmiIngredient primaryDisplayStack = EmiStack.EMPTY;
        if (primaryStack != null) {
            primaryDisplayStack = primaryStack;
        }
        widgetHolder.addSlot(primaryDisplayStack, 18, 0);
        EmiIngredient secondaryDisplayStack = EmiStack.EMPTY;
        if (secondaryStack != null) {
            secondaryDisplayStack = secondaryStack;
        }
        widgetHolder.addSlot(secondaryDisplayStack, 36, 0);
        ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
        enchantedBook.addEnchantment(output, 1);
        widgetHolder.addTexture(EmiTexture.EMPTY_ARROW, 59, 1);
        widgetHolder.addSlot(EmiStack.of(enchantedBook, 1), 88, 0);
    }
}
