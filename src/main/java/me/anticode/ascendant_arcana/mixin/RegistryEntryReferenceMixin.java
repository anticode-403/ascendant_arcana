package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.logic.RemovedRegistryEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistryEntry.Reference.class)
public abstract class RegistryEntryReferenceMixin<T> {
    @Shadow
    @Nullable
    private RegistryKey<T> registryKey;

    @Shadow
    public abstract boolean ownerEquals(RegistryEntryOwner<T> owner);

    @Inject(method = "value", at = @At("HEAD"), cancellable = true)
    private void disableEnchantments(CallbackInfoReturnable<T> cir) {
        if (registryKey != null && ownerEquals((RegistryEntryOwner<T>) Registries.ENCHANTMENT.getEntryOwner())) {
            RemovedRegistryEntry removedEntry = RemovedRegistryEntry.getFromId(registryKey.getValue());
            if (removedEntry != null) {
                cir.setReturnValue((T) removedEntry.enchantment());
            }
        }
    }
}
