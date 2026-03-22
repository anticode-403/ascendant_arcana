package me.anticode.ascendant_arcana.item;

import me.anticode.ascendant_arcana.logic.RelicHelper;
import me.anticode.ascendant_arcana.logic.Relics;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RelicItem extends Item {
    public static final String RELIC_STRENGTH_KEY = "RelicStrength";
    public static final String RELIC_TYPE_KEY = "RelicType";

    public RelicItem(Settings settings) {
        super(settings.maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public Text getName() {
        return Text.translatable("item.relics.empty");
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(getTranslationKey(), RelicHelper.getRelicStrengthName(getRelicStrength(stack)), RelicHelper.getRelicTypeName(getRelicType(stack)));
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
        Relics relicType = getRelicType(stack);
        int visualStrength = RelicHelper.getTooltipStrength(relicType, getRelicStrength(stack));
        Text relicName = RelicHelper.getRelicTypeText(relicType);
        String hasPercent = (relicType == Relics.HASTE || relicType == Relics.PROTECTION || relicType == Relics.DAMAGE) ? "%" : "";
        Text line = Text.translatable("item.relics.tooltip", visualStrength, relicName, hasPercent).formatted(Formatting.BLUE);
        String appliedToTooltip = "item.relics.tooltip.applied_any";
        if (relicType == Relics.PROTECTION) {
            appliedToTooltip = "item.relics.tooltip.applied_armor";
        }
        else if (relicType == Relics.HASTE || relicType == Relics.DAMAGE) {
            appliedToTooltip = "item.relics.tooltip.applied_tool";
        }
        tooltip.add(Text.empty());
        tooltip.add(Text.translatable(appliedToTooltip).formatted(Formatting.GRAY));
        tooltip.add(line);
    }

    public static Relics getRelicType(ItemStack stack) {
        return Relics.fromId(stack.getOrCreateNbt().getInt(RELIC_TYPE_KEY));
    }

    public static int getRelicStrength(ItemStack stack) {
        return stack.getOrCreateNbt().getInt(RELIC_STRENGTH_KEY);
    }

    public static void writeRelicData(ItemStack stack, Relics relicType, int strength) {
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putInt(RELIC_TYPE_KEY, Relics.toId(relicType));
        tag.putInt(RELIC_STRENGTH_KEY, strength);
    }
}