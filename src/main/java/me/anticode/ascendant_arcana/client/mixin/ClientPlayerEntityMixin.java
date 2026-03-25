package me.anticode.ascendant_arcana.client.mixin;

import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow @Final
    protected MinecraftClient client;

    @Shadow
    protected abstract boolean isWalking();

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z"))
    private boolean preventSprintCancelWithStrafe(Input instance) {
        if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.STRAFE, (ClientPlayerEntity)(Object)this) >= 1) return hasMovement(instance);
        return instance.hasForwardMovement();
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.2F))
    private float modifyUseMovementPenalty(float value) {
        if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.STRAFE, (ClientPlayerEntity)(Object)this) >= 1) return 0.6F;
        return value;
    }

    @Redirect(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z"))
    private boolean startSprintWithStrafe(ClientPlayerEntity instance) {
        if (!instance.isSubmergedInWater() && EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.STRAFE, instance) >= 1) {
            return (MathHelper.abs(instance.input.movementForward) >= 0.8 || MathHelper.abs(instance.input.movementSideways) >= 0.8)
                    && client.options.sprintKey.isPressed();
        }
        return isWalking();
    }

    @Unique
    private boolean hasMovement(Input instance) {
        return MathHelper.abs(instance.movementForward) > 1.0E-5F || MathHelper.abs(instance.movementSideways) > 1.0E-5F;
    }
}
