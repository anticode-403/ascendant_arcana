package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.api.EnchantedTrident;
import me.anticode.ascendant_arcana.init.AArcanaStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class TridentEntityMixin implements EnchantedTrident {
    @Unique
    private int ambushLevel;

    @Unique
    private int lifetideLevel;

    @Unique
    private int sunderingLevel;

    @Unique
    private LivingEntity stuckEntity = null;

    @Unique
    private int stuckEntityId = -1;

    @Unique
    private int ticksStuck = 0;

    @Unique
    private float renderTicks = 0;

    @Unique
    private float stabTicks = 0;

    @Override
    public LivingEntity ascendant_arcana$getStuckEntity() {
        return stuckEntity;
    }

    public float ascendant_arcana$getRenderTicks() {
        return renderTicks;
    }

    public float ascendant_arcana$getStabTicks() {
        return stabTicks;
    }

    @Override
    public void ascendant_arcana$setLifetideLevel(int value) {
        this.lifetideLevel = value;
    }

    @Override
    public void ascendant_arcana$setSunderingLevel(int value) {
        this.sunderingLevel = value;
    }

    @Override
    public void ascendant_arcana$setAmbushLevel(int value) {
        this.ambushLevel = value;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomAttributes(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("ambushLevel", ambushLevel);
        nbt.putInt("lifetideLevel", lifetideLevel);
        nbt.putInt("stuckEntityId", stuckEntityId);
        nbt.putInt("ticksStuck", ticksStuck);
        nbt.putFloat("renderTicks", renderTicks);
        nbt.putFloat("stabTicks", stabTicks);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.ambushLevel = nbt.getInt("ambushLevel");
        this.lifetideLevel = nbt.getInt("lifetideLevel");
        this.stuckEntityId = nbt.getInt("stuckEntityId");
        this.ticksStuck = nbt.getInt("ticksStuck");
        this.renderTicks = nbt.getFloat("renderTicks");
        this.stabTicks = nbt.getFloat("stabTicks");
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    private void onEntityHitHead(EntityHitResult entityHitResult, CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity)((Object)this);
        if (lifetideLevel >= 1 || sunderingLevel >= 1) {
            if (entityHitResult.getEntity() instanceof LivingEntity livingEntity && stuckEntity == null) {
                stuckEntity = livingEntity;
                stuckEntityId = livingEntity.getId();
                SoundCategory soundCategory = SoundCategory.PLAYERS;
                if (projectile.getOwner() != null) soundCategory = projectile.getOwner().getSoundCategory();
                projectile.getWorld().playSound(null, projectile.getBlockPos(), SoundEvents.ITEM_TRIDENT_HIT, soundCategory);
                if (lifetideLevel >= 1) {
                    if (projectile.getWorld() instanceof ServerWorld serverWorld) {
                        for(int i = 0; i < 5; ++i) {
                            double offset = livingEntity.getRandom().nextGaussian() * 0.02;
                            serverWorld.spawnParticles(ParticleTypes.HEART, livingEntity.offsetX(2 * livingEntity.getRandom().nextDouble() - 1), livingEntity.getRandomBodyY(), livingEntity.offsetZ(2 * livingEntity.getRandom().nextDouble() - 1), 5, offset, offset, offset, 1);
                        }
                    }
                    projectile.getWorld().playSound(null, projectile.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, soundCategory, 1, 2);
                    stuckEntity.heal(4);
                } else if (sunderingLevel >= 1) {
                    if (projectile.getWorld() instanceof ServerWorld serverWorld) {
                        for(int i = 0; i < 5; ++i) {
                            double offset = livingEntity.getRandom().nextGaussian() * 0.02;
                            serverWorld.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, livingEntity.offsetX(2 * livingEntity.getRandom().nextDouble() - 1), livingEntity.getRandomBodyY(), livingEntity.offsetZ(2 * livingEntity.getRandom().nextDouble() - 1), 5, offset, offset, offset, 1);
                        }
                    }
                    projectile.getWorld().playSound(null, projectile.getBlockPos(), SoundEvents.ENTITY_ITEM_BREAK, soundCategory, 1, 0.5F);
                    stuckEntity.addStatusEffect(new StatusEffectInstance(AArcanaStatusEffects.SUNDERED, 60, 0, true, false, true));
                    stuckEntity.damage(projectile.getDamageSources().trident(projectile, projectile.getOwner()), 2);
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "onEntityHit", at = @At("TAIL"))
    private void onEntityHitTail(EntityHitResult entityHitResult, CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity)((Object)this);
        LivingEntity owner = (LivingEntity)projectile.getOwner();
        World world = entityHitResult.getEntity().getWorld();

        if (ambushLevel >= 1 && (entityHitResult.getEntity() instanceof LivingEntity)) {
            Vec3d teleTarget = entityHitResult.getPos();
            world.playSoundFromEntity(null, owner, SoundEvents.ENTITY_ENDERMAN_TELEPORT, owner.getSoundCategory(), 1, 1);
            world.emitGameEvent(GameEvent.TELEPORT, owner.getPos(), GameEvent.Emitter.of(owner, owner.getSteppingBlockState()));
            owner.requestTeleport(teleTarget.getX(), teleTarget.getY(), teleTarget.getZ());
            world.sendEntityStatus(owner, (byte)46);
            if (owner instanceof PathAwareEntity pathAware) {
                pathAware.getNavigation().stop();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void stuckTridentEnchants(CallbackInfo ci) {
        if (lifetideLevel <= 0 && sunderingLevel <= 0) return;
        TridentEntity trident = (TridentEntity)(Object)this;
        if (stuckEntityId == -2) {
            stuckEntity = null;
            stuckEntityId = -1;
        } else if (stuckEntityId != -1 && stuckEntity == null && trident.getWorld().getEntityById(stuckEntityId) instanceof LivingEntity living) {
            stuckEntity = living;
        } else {
            if (stuckEntity != null && stuckEntity.isAlive()) {
                trident.setVelocity(Vec3d.ZERO);
                if (++ticksStuck > 120) {
                    stuckEntityId = -2;
                }
                renderTicks += 1 / 20F;
                stabTicks = Math.max(0, stabTicks - stabTicks / 20F);
            } else {
                stuckEntityId = -2;
                ticksStuck = 0;
                renderTicks = 0;
                stabTicks = 0;
            }
        }
        if (!trident.getWorld().isClient()) {
            if (stuckEntity != null && stuckEntity.isAlive()) {
                if (trident.getOwner() instanceof LivingEntity living && living.isAlive()) {
                    trident.teleport(stuckEntity.getX(), stuckEntity.getEyeY(), stuckEntity.getZ());
                    if (ticksStuck % 20 == 0) {
                        if (lifetideLevel >= 1) {
                            if (trident.getWorld() instanceof ServerWorld serverWorld) {
                                for(int i = 0; i < 5; ++i) {
                                    double offset = stuckEntity.getRandom().nextGaussian() * 0.02;
                                    serverWorld.spawnParticles(ParticleTypes.HEART, stuckEntity.offsetX(2 * stuckEntity.getRandom().nextDouble() - 1), stuckEntity.getRandomBodyY(), stuckEntity.offsetZ(2 * stuckEntity.getRandom().nextDouble() - 1), 5, offset, offset, offset, 1);
                                }
                            }
                            trident.getWorld().playSound(null, trident.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, stuckEntity.getSoundCategory(), 1, 2);
                            stuckEntity.heal(2);
                            living.heal(1);
                        } else if (sunderingLevel >= 1) {
                            if (trident.getWorld() instanceof ServerWorld serverWorld) {
                                for(int i = 0; i < 5; ++i) {
                                    double offset = stuckEntity.getRandom().nextGaussian() * 0.02;
                                    serverWorld.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, stuckEntity.offsetX(2 * stuckEntity.getRandom().nextDouble() - 1), stuckEntity.getRandomBodyY(), stuckEntity.offsetZ(2 * stuckEntity.getRandom().nextDouble() - 1), 5, offset, offset, offset, 1);
                                }
                            }
                            trident.getWorld().playSound(null, trident.getBlockPos(), SoundEvents.ENTITY_ITEM_BREAK, stuckEntity.getSoundCategory(), 1, 0.5F);
                            stuckEntity.addStatusEffect(new StatusEffectInstance(AArcanaStatusEffects.SUNDERED, 60, 0, true, false, true));
                            stuckEntity.damage(trident.getDamageSources().trident(trident, trident.getOwner()), 1);
                        }
                        stabTicks = 1;
                    }
                } else {
                    stuckEntityId = -2;
                }
            }
        } else {
            if (stuckEntity != null && stuckEntity.isAlive() && stabTicks == 19 / 20F) {
                if (MinecraftClient.getInstance().gameRenderer.getCamera().isThirdPerson() || stuckEntity != MinecraftClient.getInstance().cameraEntity) {
                    for (int i = 0; i < 6; i++) {
                        trident.getWorld().addParticle(ParticleTypes.HEART, stuckEntity.getParticleX(0.5), stuckEntity.getBodyY(0.5), stuckEntity.getParticleZ(0.5), 0, 0, 0);
                    }
                }
            }
        }
    }
}