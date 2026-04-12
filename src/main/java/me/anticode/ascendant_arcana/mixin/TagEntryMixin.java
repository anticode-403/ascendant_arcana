package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(TagEntry.class)
public class TagEntryMixin {
    @Shadow
    @Final
    private Identifier id;

    @Inject(method = "resolve", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private <T> void disableEnchantments(TagEntry.ValueGetter<T> valueGetter, Consumer<T> consumer, CallbackInfoReturnable<Boolean> cir) {
        if (!AArcanaEnchantmentHelper.isEnchantmentEnabled(id) && Registries.ENCHANTMENT.get(id) != null) {
            cir.setReturnValue(true);
        }
    }
}