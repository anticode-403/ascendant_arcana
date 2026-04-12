package me.anticode.ascendant_arcana.logic;

import me.anticode.ascendant_arcana.AscendantArcana;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class RelicHelper {
    public static final String RELICS_KEY = "AscendantArcanaRelics";

    public static final String BONUS_RELIC_CAPACITY = "AscendantArcanaRelicCapacity";

    public static int getRelicCapacity(ItemStack stack) {
        int base_capacity = AscendantArcana.config.base_relic_capacity;
        if (AscendantArcana.config.base_relic_capacity_overrides.containsKey(Registries.ITEM.getId(stack.getItem()).toString())) {
            base_capacity = AscendantArcana.config.base_relic_capacity_overrides.get(Registries.ITEM.getId(stack.getItem()).toString());
        }
        if (!stack.hasNbt()) {
            return base_capacity;
        }
        return base_capacity + stack.getOrCreateNbt().getInt(BONUS_RELIC_CAPACITY);
    }

    public static Map<Relics, Integer> fromNbt(NbtCompound nbt) {
        return fromNbtList((NbtList) nbt.get(RELICS_KEY));
    }

    public static Map<Relics, Integer> fromNbtList(NbtList list) {
        Map<Relics, Integer> map = new HashMap<>();
        if (list == null) return map;
        for  (int i = 0; i < list.size(); ++i) {
            NbtCompound tag = list.getCompound(i);
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
            tag.putInt("id", Relics.toId(entry.getKey()));
            tag.putInt("strength", entry.getValue());
            nbtList.add(tag);
        }
        return nbtList;
    }

    public static boolean canApplyRelic (ItemStack stack, Relics relic, int strength) {
        Map<Relics, Integer> relics = fromNbt(stack.getOrCreateNbt());
        if (relics.containsKey(relic) && strength > relics.get(relic)) return true;
        else return getRelicCapacity(stack) > relics.keySet().size();
    }

    public static ItemStack applyRelic(ItemStack stack, Relics relicType, int strength) {
        Map<Relics, Integer> relics = fromNbt(stack.getOrCreateNbt());
        relics.put(relicType, strength);
        stack.getOrCreateNbt().putInt(RELICS_KEY, relics.size());
        return stack;
    }

    public static int getValueFromNbt(NbtCompound nbt, Relics key) {
        if (nbt == null) return 0;
        Map<Relics, Integer> map = fromNbt(nbt);
        return map.get(key) != null ? map.get(key) : 0;
    }

    public static int getTooltipStrength(Relics relicType, int strength) {
        if (strength == 0) return 0;
        return switch (relicType) {
            case DAMAGE -> strength <= 3 ? 8 + strength * 4 : 10 + strength * 4;
            case DURABILITY -> strength * 600;
            case PROTECTION -> strength * 3;
            case HASTE -> strength * 10;
            case ENCHANTMENT_CAPACITY -> 5 + strength * 5;
        };
    }

    public static Text getRelicTypeText(Relics relicType) {
        return Text.translatable("item.relics.type." + relicType.toString().toLowerCase());
    }

    public static Text getRelicStrengthName(int strength) {
        return Text.translatable("item.relics.strength." + strength);
    }

    public static Text getRelicTypeName(Relics relicType) {
        return Text.translatable("item.relics.name." + relicType.toString().toLowerCase());
    }
}
