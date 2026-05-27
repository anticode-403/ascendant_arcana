package me.anticode.ascendant_arcana.client.emi.recipes;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmiGrindstoneScrapRecipe implements EmiRecipe {
    private static final Identifier BACKGROUND = new Identifier("minecraft", "textures/gui/container/grindstone.png");
    private final int uniq = new Random().nextInt();
    private final Item tool;
    private final Identifier id;

    public EmiGrindstoneScrapRecipe(Item tool, Identifier id) {
        this.tool = tool;
        this.id = id;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.GRINDING;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiStack.of(tool));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(AArcanaItems.ENCHANTED_SCRAP));
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public int getDisplayWidth() {
        return 116;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, 0, 0, 116, 56, 30, 15);

        widgets.addGeneratedSlot(r -> getToolOrScrap(r, false), uniq, 18, 3).drawBack(false);
        widgets.addGeneratedSlot(r -> getToolOrScrap(r, true), uniq, 98, 18).drawBack(false).recipeContext(this);
    }

    private EmiStack getToolOrScrap(Random random, Boolean scrap) {
        ItemStack itemStack = new ItemStack(tool);
        int enchantments = 1 + Math.max(random.nextInt(5), random.nextInt(3));

        List<Enchantment> list = Lists.newArrayList();

        outer:
        for (int i = 0; i < enchantments; i++) {
            Enchantment enchantment = getEnchantment(random);

            int maxLvl = enchantment.getMaxLevel();
            int minLvl = enchantment.getMinLevel();
            // Some enchantments are returning zero for max level? I don't want to think about it
            int lvl = maxLvl > 0 ? random.nextInt(maxLvl) + 1 : 0;

            if (lvl < minLvl) {
                lvl = minLvl;
            }

            for (Enchantment e : list) {
                if (e == enchantment || !e.canCombine(enchantment)) {
                    continue outer;
                }
            }
            list.add(enchantment);

            itemStack.addEnchantment(enchantment, lvl);
        }
        Map<Enchantment, Integer> appliedEnchants = EnchantmentHelper.get(itemStack);
        if (scrap) {
            return EmiStack.of(AArcanaEnchantmentHelper.convertEnchantmentsToScrap(appliedEnchants));
        }
        return EmiStack.of(itemStack);
    }

    private Enchantment getEnchantment(Random random){
        List<Enchantment> enchantments = Registries.ENCHANTMENT.stream().filter(i -> i.isAcceptableItem(tool.getDefaultStack())).toList();
        int enchantment = random.nextInt(enchantments.size());
        return enchantments.get(enchantment);
    }
}
