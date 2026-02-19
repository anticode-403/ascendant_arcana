package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.init.AArcanaAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract double getAttributeValue(EntityAttribute attribute);

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static DefaultAttributeContainer.Builder createLivingAttributes(DefaultAttributeContainer.Builder original) {
        original.add(AArcanaAttributes.PROTECTION);
        return original;
    }

    @ModifyReturnValue(method = "modifyAppliedDamage", at = @At("RETURN"))
    private float applyProtectionStat(float original, @Local(argsOnly = true) DamageSource source) {
        if (source.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)) return original;
        double protectionStrength = getAttributeValue(AArcanaAttributes.PROTECTION);
        float multiplier = 2F - (float) protectionStrength;
        return original * multiplier;
    }
}
