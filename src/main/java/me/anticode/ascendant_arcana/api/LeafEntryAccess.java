package me.anticode.ascendant_arcana.api;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunction;

public interface LeafEntryAccess {
    int ascendantArcana$getWeight();

    int ascendantArcana$getQuality();

    LootFunction[] ascendantArcana$getFunctions();
}
