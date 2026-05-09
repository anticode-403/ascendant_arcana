package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "usageTick", at = @At("HEAD"))
    private void cleanseShield(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci) {
        if (!((Object) this instanceof ShieldItem && user instanceof PlayerEntity player)) return;
        if (EnchantmentHelper.getLevel(AArcanaEnchantments.CLEANSE, stack) > 0) {
            int usageTicks = 72000 - remainingUseTicks;
            if (usageTicks >= 20 && !user.getStatusEffects().isEmpty()) {
                user.clearStatusEffects();
                player.stopUsingItem();
                player.getItemCooldownManager().set(stack.getItem(), 200);
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, player.getSoundCategory(), 0.7F, 2);
            }
        } else if (EnchantmentHelper.getLevel(AArcanaEnchantments.SONIC_BLAST, stack) > 0) {
            int usageTicks = 72000 - remainingUseTicks;
            if (usageTicks == 20) {
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_CHARGE, player.getSoundCategory(), 2F, 0.8F);
            } else if (usageTicks >= 54) {
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, player.getSoundCategory(), 2F, 0.8F);
                Vec3d lookDir = Vec3d.fromPolar(player.getPitch(), player.getYaw());
                Vec3d startPos = player.getEyePos().add(lookDir.multiply(0.5));
                Vec3d endPos = startPos.add(lookDir.normalize().multiply(12.5));
                for(int i = 1; i < 17; ++i) {
                    double delta = ((double)i) / 17;
                    Vec3d particlePos = startPos.lerp(endPos, delta);
                    for (Entity entity : world.getOtherEntities(player, new Box(particlePos.subtract(0.5, 0.5, 0.5), particlePos.add(0.5, 0.5, 0.5)), (entity) -> entity instanceof LivingEntity)) {
                        LivingEntity livingEntity = (LivingEntity)entity;
                        livingEntity.damage(world.getDamageSources().sonicBoom(player), 6);
                    }
                    if (world instanceof ServerWorld serverWorld) serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0F, 0.0F, 0.0F, 0.0F);
                }
                player.stopUsingItem();
                player.getItemCooldownManager().set(stack.getItem(), 100);
            }
        }
    }
}
