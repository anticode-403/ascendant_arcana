package me.anticode.ascendant_arcana.item;

import me.anticode.ascendant_arcana.logic.RelicHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.of("Relic Type: " + getRelicType(stack)));
        tooltip.add(Text.of("Relic Strength: " + getRelicStrength(stack)));
    }

    public static RelicHelper.Relics getRelicType(ItemStack stack) {
        return RelicHelper.Relics.fromId(stack.getOrCreateNbt().getInt(RELIC_TYPE_KEY));
    }

    public static int getRelicStrength(ItemStack stack) {
        return stack.getOrCreateNbt().getInt(RELIC_STRENGTH_KEY);
    }

    public static void writeRelicData(ItemStack stack, RelicHelper.Relics relicType, int strength) {
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putInt(RELIC_TYPE_KEY, RelicHelper.Relics.toId(relicType));
        tag.putInt(RELIC_STRENGTH_KEY, strength);
    }
}