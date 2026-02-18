package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.anticode.ascendant_arcana.logic.RelicHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract NbtCompound getOrCreateNbt();

    @ModifyReturnValue(method = "getMaxDamage", at = @At("RETURN"))
    private int implementDurabilityRelic(int maxDamage) {
        return maxDamage + RelicHelper.getValueFromNbt((NbtList)getOrCreateNbt().get(RelicHelper.AARELICS_KEY), RelicHelper.Relics.DURABILITY) * 300;
    }


}
