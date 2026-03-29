package me.anticode.ascendant_arcana.recipe;

import com.google.gson.JsonObject;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class EnchantmentRecipe implements Recipe<Inventory> {
    private final Identifier id;
    final int magicalScrapCost;
    final Ingredient primaryIngredient;
    final Ingredient secondaryIngredient;
    final int levelCost;
    final Enchantment enchantment;

    EnchantmentRecipe(Identifier id, int magicalScrapCost, Ingredient primaryIngredient, Ingredient secondaryIngredient, int levelCost, Enchantment enchantment) {
        this.id = id;
        this.magicalScrapCost = magicalScrapCost;
        this.primaryIngredient = primaryIngredient;
        this.secondaryIngredient = secondaryIngredient;
        this.levelCost = levelCost;
        this.enchantment = enchantment;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 1;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return null;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AArcanaRecipes.ENCHANTMENT_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return AArcanaRecipes.ENCHANTMENT_RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<EnchantmentRecipe> {

        @Override
        public EnchantmentRecipe read(Identifier id, JsonObject json) {
            int magicalScrapCost = json.get("magical_scrap_cost").getAsInt();
            Ingredient primaryIngredient = Ingredient.fromJson(json.get("primary_ingredient"));
            Ingredient secondaryIngredient = Ingredient.fromJson(json.get("secondary_ingredient"));
            int levelCost = json.get("level_cost").getAsInt();
            Enchantment enchantment = Registries.ENCHANTMENT.getOrEmpty(Identifier.tryParse(json.get("enchantment").getAsString())).orElse(null);
            return new EnchantmentRecipe(id, magicalScrapCost, primaryIngredient, secondaryIngredient, levelCost, enchantment);
        }

        @Override
        public EnchantmentRecipe read(Identifier id, PacketByteBuf buf) {
            int magicalScrapCost = buf.readInt();
            Ingredient primaryIngredient = Ingredient.fromPacket(buf);
            Ingredient secondaryIngredient = Ingredient.fromPacket(buf);
            int levelCost = buf.readInt();
            Enchantment enchantment = Registries.ENCHANTMENT.getOrEmpty(Identifier.tryParse(buf.readString())).orElse(null);
            return new EnchantmentRecipe(id, magicalScrapCost, primaryIngredient, secondaryIngredient, levelCost, enchantment);
        }

        @Override
        public void write(PacketByteBuf buf, EnchantmentRecipe recipe) {
            buf.writeInt(recipe.magicalScrapCost);
            recipe.primaryIngredient.write(buf);
            recipe.secondaryIngredient.write(buf);
            buf.writeInt(recipe.levelCost);
            buf.writeString(Registries.ENCHANTMENT.getId(recipe.enchantment).toString());
        }
    }
}
