package me.anticode.ascendant_arcana.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import me.anticode.ascendant_arcana.init.AArcanaLootFunctionTypes;
import me.anticode.ascendant_arcana.item.RelicItem;
import me.anticode.ascendant_arcana.logic.RelicHelper;
import me.anticode.ascendant_arcana.logic.Relics;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class PopulateRelicLootFunction extends ConditionalLootFunction{
    final LootNumberProvider strength;
    final int[] relicTypes;

    protected PopulateRelicLootFunction(LootCondition[] conditions, LootNumberProvider strength, int[] relicTypes) {
        super(conditions);
        this.strength = strength;
        this.relicTypes = relicTypes;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        Random random = context.getRandom();
        List<Relics> relics = Lists.newArrayList();
        int str = strength.nextInt(context);
        for (int relicType : this.relicTypes) {
            Relics relic = Relics.fromId(relicType);
            if (RelicHelper.canApplyRelic(stack, relic, str)) relics.add(relic);
        }
        stack.getOrCreateNbt().putInt(RelicItem.RELIC_STRENGTH_KEY, str);
        stack.getOrCreateNbt().putInt(RelicItem.RELIC_TYPE_KEY, Relics.toId(relics.get(random.nextBetween(0, relics.size() - 1))));
        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return AArcanaLootFunctionTypes.POPULATE_RELIC;
    }

    public static PopulateRelicLootFunction.Builder builder(LootNumberProvider strength, int[] relicTypes) {
        return new PopulateRelicLootFunction.Builder(strength, relicTypes);
    }

    public static class Builder extends ConditionalLootFunction.Builder<PopulateRelicLootFunction.Builder> {
        public final LootNumberProvider strength;
        public final int[] relicTypes;

        public Builder(LootNumberProvider strength, int[] relicTypes) {
            this.strength = strength;
            this.relicTypes = relicTypes;
        }

        @Override
        public LootFunction build() {
            return new PopulateRelicLootFunction(this.getConditions(), this.strength, this.relicTypes);
        }

        @Override
        protected PopulateRelicLootFunction.Builder getThisBuilder() {
            return this;
        }
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<PopulateRelicLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, PopulateRelicLootFunction conditionalLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, conditionalLootFunction, jsonSerializationContext);
            jsonObject.add("strength", jsonSerializationContext.serialize(conditionalLootFunction.strength));
            JsonArray serializedArray = new JsonArray();
            for (int value : conditionalLootFunction.relicTypes) {
                serializedArray.add(value);
            }
            jsonObject.add("relics", serializedArray);
        }

        @Override
        public PopulateRelicLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext context, LootCondition[] conditions) {
            LootNumberProvider strength = JsonHelper.deserialize(jsonObject, "strength", context, LootNumberProvider.class);
            JsonArray array = JsonHelper.getArray(jsonObject, "relics");
            int[] relicTypes = new int[array.size()];
            for (int i = 0; i < array.size(); i++) {
                relicTypes[i] = array.get(i).getAsInt();
            }
            return new PopulateRelicLootFunction(conditions, strength, relicTypes);
        }
    }
}
