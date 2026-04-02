package me.anticode.ascendant_arcana.recipe;

import com.google.gson.JsonObject;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class EnchantmentRecipe implements Recipe<Inventory> {
    private final Identifier id;
    public final int magicalScrapCost;
    public final IngredientStack primaryIngredientStack;
    public final IngredientStack secondaryIngredientStack;
    public final int levelCost;
    public final Enchantment enchantment;

    EnchantmentRecipe(Identifier id, int magicalScrapCost, IngredientStack primaryIngredientStack, IngredientStack secondaryIngredientStack, int levelCost, Enchantment enchantment) {
        this.id = id;
        this.magicalScrapCost = magicalScrapCost;
        this.primaryIngredientStack = primaryIngredientStack;
        this.secondaryIngredientStack = secondaryIngredientStack;
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
            IngredientStack primaryIngredientStack = IngredientStack.fromJson((JsonObject) json.get("primary_ingredient"));
            IngredientStack secondaryIngredientStack = IngredientStack.fromJson((JsonObject) json.get("secondary_ingredient"));
            int levelCost = json.get("level_cost").getAsInt();
            Enchantment enchantment = Registries.ENCHANTMENT.getOrEmpty(Identifier.tryParse(json.get("enchantment").getAsString())).orElse(null);
            return new EnchantmentRecipe(id, magicalScrapCost, primaryIngredientStack, secondaryIngredientStack, levelCost, enchantment);
        }

        @Override
        public EnchantmentRecipe read(Identifier id, PacketByteBuf buf) {
            int magicalScrapCost = buf.readInt();
            IngredientStack primaryIngredientStack = IngredientStack.fromPacket(buf);
            IngredientStack secondaryIngredientStack = IngredientStack.fromPacket(buf);
            int levelCost = buf.readInt();
            Enchantment enchantment = Registries.ENCHANTMENT.getOrEmpty(Identifier.tryParse(buf.readString())).orElse(null);
            return new EnchantmentRecipe(id, magicalScrapCost, primaryIngredientStack, secondaryIngredientStack, levelCost, enchantment);
        }

        @Override
        public void write(PacketByteBuf buf, EnchantmentRecipe recipe) {
            buf.writeInt(recipe.magicalScrapCost);
            recipe.primaryIngredientStack.write(buf);
            recipe.secondaryIngredientStack.write(buf);
            buf.writeInt(recipe.levelCost);
            buf.writeString(Registries.ENCHANTMENT.getId(recipe.enchantment).toString());
        }
    }
}
