package me.anticode.ascendant_arcana.networking;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.recipe.EnchantmentRecipe;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;

public record EnchantingScreenSendRecipe(int syncId, EnchantmentRecipe recipe) {
    public static Identifier Id = new Identifier(AscendantArcana.modID, "enchanting_screen_send_recipe");

    public PacketByteBuf write() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(syncId);
        buf.writeString(recipe.getId().toString());
        return buf;
    }

    public static EnchantingScreenSendRecipe read(PacketByteBuf buf, RecipeManager recipeManager) {
        int syncId = buf.readInt();
        String id = buf.readString();
        EnchantmentRecipe recipe = (EnchantmentRecipe) recipeManager.get(Identifier.tryParse(id)).get();
        return new EnchantingScreenSendRecipe(syncId, recipe);
    }
}
