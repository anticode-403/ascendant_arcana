package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.api.ItemEntryAccess;
import net.minecraft.item.Item;
import net.minecraft.loot.entry.ItemEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntry.class)
public abstract class ItemEntryAccessor implements ItemEntryAccess {
    @Final
    @Shadow
    Item item;

    @Override
    public Item ascendantArcana$getItem() {
        return this.item;
    }
}
