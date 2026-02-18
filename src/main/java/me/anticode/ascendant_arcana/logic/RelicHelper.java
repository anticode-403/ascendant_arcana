package me.anticode.ascendant_arcana.logic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

public class RelicHelper {
    @Unique
    public static final String AARELICS_KEY = "AscendantArcanaRelics";

    public enum Relics {
        DAMAGE(0),
        DURABILITY(1),
        PROTECTION(2),
        HASTE(3),
        ENCHANTMENT_CAPACITY(4);

        private final int value;

        Relics(int relic) {
            this.value = relic;
        }

        public static Relics fromId(int value) {
            for (Relics r : values()) {
                if (r.value == value) return r;
            }
            return null;
        }

        public static int toId(Relics relic) {
            return relic.value;
        }
    }

    public static Map<Relics, Integer> fromNbt(NbtList nbt) {
        Map<Relics, Integer> map = new HashMap<>();
        if (nbt == null) return map;
        for  (int i = 0; i < nbt.size(); ++i) {
            NbtCompound tag = nbt.getCompound(i);
            Relics key = Relics.fromId(tag.getInt("id"));
            int value = tag.getInt("strength");
            map.put(key, value);
        }
        return map;
    }

    public static NbtList toNbt(Map<Relics, Integer> map) {
        NbtList nbtList = new NbtList();
        for(Map.Entry<Relics, Integer> entry : map.entrySet()) {
            NbtCompound tag = new NbtCompound();
            tag.putInt("id", entry.getKey().value);
            tag.putInt("strength", entry.getValue());
            nbtList.add(tag);
        }
        return nbtList;
    }

    public static int getValueFromNbt(NbtList nbt, Relics key) {
        if (nbt == null) return 0;
        Map<Relics, Integer> map = fromNbt(nbt);
        return map.get(key) != null ? map.get(key) : 0;
    }

    public static int convertStrengthIntoReal(Relics relicType, int strength) {
        if (strength == 0) return 0;
        return switch (relicType) {
            case DAMAGE -> strength * 2;
            case DURABILITY -> strength * 300;
            case PROTECTION -> strength * 3;
            case HASTE -> strength * 10;
            case ENCHANTMENT_CAPACITY -> strength;
        };
    }
}
