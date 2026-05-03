package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {
    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void reflectIfBlocked(HitResult hitResult, CallbackInfo ci) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            if (!(entityHitResult.getEntity() instanceof LivingEntity target)) return;
            if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.DEFLECT, target) <= 0) return;
            ProjectileEntity projectile = (ProjectileEntity)(Object)this;
            LivingEntity owner = projectile.getOwner() instanceof LivingEntity ? (LivingEntity) projectile.getOwner() : target;
            if (projectile instanceof ExplosiveProjectileEntity explosiveProjectile) {
                if (target.blockedByShield(explosiveProjectile.getDamageSources().mobProjectile(projectile, owner))) {
                    projectile.setVelocity(target, target.getPitch() - 1, target.getYaw(), 0, (float)projectile.getVelocity().length(), 0.5F);
                    projectile.setOwner(target);
                    Vec3d velocity = projectile.getVelocity();
                    double d = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getY() * velocity.getY() + velocity.getZ() * velocity.getZ());
                    if (d != (double)0.0F) {
                        explosiveProjectile.powerX = velocity.getX() / d * 0.1;
                        explosiveProjectile.powerY = velocity.getY() / d * 0.1;
                        explosiveProjectile.powerZ = velocity.getZ() / d * 0.1;
                    }
                    target.damageShield(3);
                    target.getWorld().playSound(null, target.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, target.getSoundCategory(), 1, 1);
                    ci.cancel();
                }
            } else if (target.blockedByShield(projectile.getDamageSources().mobProjectile(projectile, owner))) {
                projectile.setVelocity(target, target.getPitch() - 1, target.getYaw(), 0, (float)projectile.getVelocity().length(), 0.5F);
                projectile.setOwner(target);
                target.damageShield(1);
                target.getWorld().playSound(null, target.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, target.getSoundCategory(), 1, 1);
                ci.cancel();
            }
        }
    }
}
