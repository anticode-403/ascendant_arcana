package me.anticode.ascendant_arcana.item;

import me.anticode.ascendant_arcana.logic.RelicHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class RelicItem extends Item {
    private static final String RELIC_STRENGTH_KEY = "RelicStrength";
    private static final String RELIC_TYPE_KEY = "RelicType";

    public RelicItem(Settings settings) {
        super(settings.maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    public static RelicHelper.Relics getRelicType(ItemStack stack) {
        return RelicHelper.Relics.DURABILITY;
//        return RelicHelper.Relics.fromId(stack.getOrCreateNbt().getInt(RELIC_TYPE_KEY));
    }

    public static int getRelicStrength(ItemStack stack) {
        return 1;
//        return stack.getOrCreateNbt().getInt(RELIC_STRENGTH_KEY);
    }
}