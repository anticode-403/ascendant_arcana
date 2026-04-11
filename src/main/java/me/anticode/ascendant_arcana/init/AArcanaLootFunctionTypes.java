package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.loot.ApplyRelicLootFunction;
import me.anticode.ascendant_arcana.loot.PopulateRelicLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;

public class AArcanaLootFunctionTypes {
    public static final LootFunctionType APPLY_RELICS = register("apply_relics", new ApplyRelicLootFunction.Serializer());
    public static final LootFunctionType POPULATE_RELIC = register("populate_relic", new PopulateRelicLootFunction.Serializer());

    private static LootFunctionType register(String id, JsonSerializer<? extends LootFunction> jsonSerializer) {
        return (LootFunctionType) Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier(id), new LootFunctionType(jsonSerializer));
    }

    public static void initialize() {}
}
