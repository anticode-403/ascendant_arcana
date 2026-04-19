package me.anticode.ascendant_arcana.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import me.anticode.ascendant_arcana.item.RelicItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class RelicCraftingRecipe extends SpecialCraftingRecipe {
    final String group;
    final int strength;
    final int relic;
    final DefaultedList<Ingredient> input;

    public RelicCraftingRecipe(Identifier id, String group, CraftingRecipeCategory category, int strength, int relic, DefaultedList<Ingredient> input) {
        super(id, category);
        this.group = group;
        this.strength = strength;
        this.relic = relic;
        this.input = input;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int i = 0;
        boolean matches = true;

        for (int j = 0; j < inventory.size(); ++j) {
            ItemStack itemStack = inventory.getStack(j);
            if (!itemStack.isEmpty()) {
                ++i;
                boolean matchAny = false;
                for (Ingredient ingredient : input) {
                    if (ingredient.test(itemStack)) {
                        matchAny = true;
                    }
                }
                if (!matchAny) {
                    matches = false;
                    break;
                }
            }
        }
        return i == input.size() && matches;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack itemStack = new ItemStack(AArcanaItems.RELIC);
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt(RelicItem.RELIC_STRENGTH_KEY, strength);
        nbt.putInt(RelicItem.RELIC_TYPE_KEY, relic);
        return itemStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= input.size();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AArcanaRecipes.RELIC_CRAFTING_RECIPE_SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<RelicCraftingRecipe> {
        @Override
        public RelicCraftingRecipe read(Identifier id, JsonObject json) {
            String group = JsonHelper.getString(json, "group", "");
            int strength = JsonHelper.getInt(json, "strength", 0);
            int relic = JsonHelper.getInt(json, "relic", 0);
            CraftingRecipeCategory craftingRecipeCategory = (CraftingRecipeCategory)CraftingRecipeCategory.CODEC.byId(JsonHelper.getString(json, "category", null), CraftingRecipeCategory.MISC);
            DefaultedList<Ingredient> defaultedList = getIngredients(JsonHelper.getArray(json, "ingredients"));
            if (defaultedList.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (defaultedList.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            } else {
                return new RelicCraftingRecipe(id, group, craftingRecipeCategory, strength, relic, defaultedList);
            }
        }

        private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
            DefaultedList<Ingredient> defaultedList = DefaultedList.of();

            for(int i = 0; i < json.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(json.get(i), false);
                if (!ingredient.isEmpty()) {
                    defaultedList.add(ingredient);
                }
            }

            return defaultedList;
        }

        @Override
        public RelicCraftingRecipe read(Identifier id, PacketByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            int strength = buf.readInt();
            int relic = buf.readInt();
            int i = buf.readVarInt();

            DefaultedList<Ingredient> list = DefaultedList.ofSize(i, Ingredient.EMPTY);
            list.replaceAll(ignored -> Ingredient.fromPacket(buf));

            return new RelicCraftingRecipe(id, group, category, strength, relic, list);
        }

        @Override
        public void write(PacketByteBuf buf, RelicCraftingRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.getCategory());
            buf.writeInt(recipe.strength);
            buf.writeInt(recipe.relic);
            buf.writeVarInt(recipe.input.size());

            for (Ingredient ingredient : recipe.input) {
                ingredient.write(buf);
            }
        }
    }
}
