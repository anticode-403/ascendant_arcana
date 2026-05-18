package me.anticode.ascendant_arcana.networking;

import me.anticode.ascendant_arcana.AscendantArcana;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record EnchantingScreenRemoveRecipe(int syncId) {
    public static Identifier Id = new Identifier(AscendantArcana.modID, "enchanting_screen_remove_recipe");

    public PacketByteBuf write() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(syncId);
        return buf;
    }

    public static EnchantingScreenRemoveRecipe read(PacketByteBuf buf) {
        int syncId = buf.readInt();
        return new EnchantingScreenRemoveRecipe(syncId);
    }
}
