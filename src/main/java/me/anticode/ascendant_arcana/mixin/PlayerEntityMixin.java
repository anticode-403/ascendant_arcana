package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.anticode.ascendant_arcana.logic.RelicHelper;
import me.anticode.ascendant_arcana.logic.Relics;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
                float hasteMultiplier = 1 - ((float)RelicHelper.getTooltipStrength(Relics.HASTE, relics.get(Relics.HASTE)) * 0.01F);
                return original * hasteMultiplier;
            }
        }
        return original;
    }
}
