package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import me.anticode.ascendant_arcana.init.AArcanaStatusEffects;
import me.anticode.ascendant_arcana.logic.RelicHelper;
import me.anticode.ascendant_arcana.logic.Relics;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @ModifyReturnValue(method = "getAttackCooldownProgressPerTick", at = @At("RETURN"))
    private float modifyAttackCooldownProgress(float original) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        ItemStack mainStack = livingEntity.getMainHandStack();
        if (mainStack.getItem() instanceof ToolItem) {
            Map<Relics, Integer> relics = RelicHelper.fromNbt(mainStack.getOrCreateNbt());
            if (relics.containsKey(Relics.HASTE)) {
                float hasteMultiplier = 1 - ((float)RelicHelper.getTooltipStrength(Relics.HASTE, relics.get(Relics.HASTE)) * 0.005F);
                return original * hasteMultiplier;
            }
        }
        return original;
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), cancellable = true)
    private void protectiveEcho(DamageSource source, float amount, CallbackInfo ci) {
        if (amount < 5) return;
        if (((LivingEntity)(Object)this).getStatusEffect(AArcanaStatusEffects.ECHOING_DAMAGE) != null) return;
        if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.PROTECTIVE_ECHO, (LivingEntity) (Object) this) == 0) return;
        int duration = 100 * Math.max((int)amount / 10, 1);
        int strength = Math.max((int)amount / (duration / 20), 1);
        ((LivingEntity)(Object)this).setStatusEffect(new StatusEffectInstance(AArcanaStatusEffects.ECHOING_DAMAGE, duration + 20, strength), (LivingEntity)(Object)this);
        ci.cancel();
    }
}
