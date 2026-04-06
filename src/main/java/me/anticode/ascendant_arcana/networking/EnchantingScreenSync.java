package me.anticode.ascendant_arcana.networking;

import com.google.common.collect.Lists;
import me.anticode.ascendant_arcana.AscendantArcana;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public record EnchantingScreenSync(int syncId, List<Enchantment> treasures) {
    public static Identifier Id = new Identifier(AscendantArcana.modID, "enchanting_screen_sync");

    public PacketByteBuf write() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(syncId);
        buf.writeInt(treasures.size());
        for (Enchantment enchantment : treasures) {
            buf.writeString(Registries.ENCHANTMENT.getId(enchantment).toString());
        }
        return buf;
    }

    public static EnchantingScreenSync read(PacketByteBuf buf) {
        int syncId = buf.readInt();
        int i = buf.readInt();
        List<Enchantment> treasures = Lists.newArrayList();
        for (int j = 0; j < i; ++j) {
            Identifier enchantId = Identifier.tryParse(buf.readString());
            Enchantment enchantment = Registries.ENCHANTMENT.get(enchantId);
            treasures.add(enchantment);
        }
        return new EnchantingScreenSync(syncId, treasures);
    }
}
