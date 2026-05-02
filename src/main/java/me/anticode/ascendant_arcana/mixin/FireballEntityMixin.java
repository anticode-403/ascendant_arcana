package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireballEntity.class)
public class FireballEntityMixin {
    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/AbstractFireballEntity;onCollision(Lnet/minecraft/util/hit/HitResult;)V", shift = At.Shift.AFTER), cancellable = true)
    private void cancelOnHitingDeflect(HitResult hitResult, CallbackInfo ci) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            if (AArcanaEnchantmentHelper.doesEntityHitHaveDeflect((EntityHitResult)hitResult, (ProjectileEntity)(Object)this)) {
                ci.cancel();
            }
        }
    }
}
