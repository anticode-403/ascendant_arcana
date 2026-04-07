package me.anticode.ascendant_arcana.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import me.anticode.ascendant_arcana.init.AArcanaLootFunctionTypes;
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

public class ApplyRelicLootFunction extends ConditionalLootFunction {
    final LootNumberProvider count;
    final LootNumberProvider strength;
    final int[] relicTypes;

    protected ApplyRelicLootFunction(LootCondition[] conditions, LootNumberProvider count, LootNumberProvider strength, int[] relicTypes) {
        super(conditions);
        this.count = count;
        this.strength = strength;
        this.relicTypes = relicTypes;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        Random random = context.getRandom();
        List<Relics> relics = Lists.newArrayList();
        int total = count.nextInt(context);
        if (RelicHelper.getRelicCapacity(stack) > total) total = RelicHelper.getRelicCapacity(stack);
        for (int i = 0; i < total; i++) {
            int nextStr = strength.nextInt(context);
            for (int relicType : this.relicTypes) {
                Relics relic = Relics.fromId(relicType);
                if (RelicHelper.canApplyRelic(stack, relic, nextStr)) relics.add(relic);
            }
            Relics nextRelic = relics.get(random.nextBetween(0, relics.size() - 1));
            RelicHelper.applyRelic(stack, nextRelic, nextStr);
        }
        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return AArcanaLootFunctionTypes.APPLY_RELICS;
    }

    public static Builder builder(LootNumberProvider count, LootNumberProvider strength, int[] relicTypes) {
        return new Builder(count, strength, relicTypes);
    }

    public static class Builder extends ConditionalLootFunction.Builder<Builder> {
        final LootNumberProvider count;
        public final LootNumberProvider strength;
        public final int[] relicTypes;

        public Builder(LootNumberProvider count, LootNumberProvider strength, int[] relicTypes) {
            this.count = count;
            this.strength = strength;
            this.relicTypes = relicTypes;
        }

        @Override
        public LootFunction build() {
            return new ApplyRelicLootFunction(this.getConditions(), this.count, this.strength, this.relicTypes);
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<ApplyRelicLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, ApplyRelicLootFunction conditionalLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, conditionalLootFunction, jsonSerializationContext);
            jsonObject.add("count", jsonSerializationContext.serialize(conditionalLootFunction.count));
            jsonObject.add("strength", jsonSerializationContext.serialize(conditionalLootFunction.strength));
            JsonArray serializedArray = new JsonArray();
            for (int value : conditionalLootFunction.relicTypes) {
                serializedArray.add(value);
            }
            jsonObject.add("relics", serializedArray);
        }

        @Override
        public ApplyRelicLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext context, LootCondition[] conditions) {
            LootNumberProvider count = JsonHelper.deserialize(jsonObject, "count", context, LootNumberProvider.class);
            LootNumberProvider strength = JsonHelper.deserialize(jsonObject, "strength", context, LootNumberProvider.class);
            JsonArray array = JsonHelper.getArray(jsonObject, "relics");
            int[] relicTypes = new int[array.size()];
            for (int i = 0; i < array.size(); i++) {
                relicTypes[i] = array.get(i).getAsInt();
            }
            return new ApplyRelicLootFunction(conditions, count, strength, relicTypes);
        }
    }
}
