package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.recipe.InfusionRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class AArcanaRecipes {
    public static RecipeSerializer<InfusionRecipe> INFUSION_RECIPE_SERIALIZER = register("infusion_smithing_recipe", new InfusionRecipe.Serializer());

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
        return (S)(Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(AscendantArcana.modID, name), serializer));
    }

    public static void initialize() {}
}
