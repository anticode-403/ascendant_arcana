package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.recipe.EnchantmentRecipe;
import me.anticode.ascendant_arcana.recipe.InfusionRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class AArcanaRecipes {
    public static RecipeSerializer<InfusionRecipe> INFUSION_RECIPE_SERIALIZER = register("infusion_smithing_recipe", new InfusionRecipe.Serializer());
    public static RecipeSerializer<EnchantmentRecipe> ENCHANTMENT_RECIPE_SERIALIZER = register("enchantment_smithing_recipe", new EnchantmentRecipe.Serializer());

    public static RecipeType<EnchantmentRecipe> ENCHANTMENT_RECIPE_TYPE = register("enchantment_recipe");

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
        return (S)(Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(AscendantArcana.modID, name), serializer));
    }

    static <T extends Recipe<?>> RecipeType register(final String id) {
        return Registry.register(Registries.RECIPE_TYPE, Identifier.of(AscendantArcana.modID, id), new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }

    public static void initialize() {}
}
