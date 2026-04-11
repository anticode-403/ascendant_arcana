package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.api.LeafEntryAccess;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LeafEntry.class)
public class LeafEntryAccessor implements LeafEntryAccess {
    @Shadow
    @Final
    protected int weight;

    @Shadow
    @Final
    protected int quality;

    @Shadow
    @Final
    protected LootFunction[] functions;

    @Override
    public int ascendantArcana$getWeight() {
        return this.weight;
    }

    @Override
    public int ascendantArcana$getQuality() {
        return this.quality;
    }

    @Override
    public LootFunction[] ascendantArcana$getFunctions() {
        return this.functions;
    }
}
