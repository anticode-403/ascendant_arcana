package me.anticode.ascendant_arcana.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;

import java.util.function.Predicate;

public class IngredientStack implements Predicate<ItemStack> {
    private final Ingredient ingredient;
    private final int count;

    public IngredientStack(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return ingredient.test(itemStack) && count <= itemStack.getCount();
    }

    public void write (PacketByteBuf buf) {
        ingredient.write(buf);
        buf.writeInt(count);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("ingredient", ingredient.toJson());
        json.addProperty("count", count);
        return json;
    }

    public static IngredientStack fromPacket (PacketByteBuf buf) {
        Ingredient ingredient = Ingredient.fromPacket(buf);
        int count = buf.readInt();
        return new IngredientStack(ingredient, count);
    }

    public static IngredientStack fromJson (JsonObject json) {
        if (json == null || json.isJsonNull()) return null;
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
        int count = json.get("count").getAsInt();
        return new IngredientStack(ingredient, count);
    }
}
