package me.anticode.ascendant_arcana.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.api.EnchantedArrow;
import me.anticode.ascendant_arcana.init.AArcanaStatusEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin implements EnchantedArrow {

    @Shadow
    protected boolean inGround;

    @Shadow
    public abstract void setCritical(boolean critical);

    @Shadow
    @Nullable
    private BlockState inBlockState;

    @Shadow
    public abstract byte getPierceLevel();

    @Shadow
    protected abstract void onHit(LivingEntity target);

    @Unique
    private int archersGambitLevel;

    @Unique
    private int evokersWrathLevel;

    @Unique
    private int ricochetLevel;

    @Unique
    private int ricochetBounces = 0;

    @Unique
    private boolean ricochet;

    @Unique
    @Nullable
    private Vec3d ricochetVector;

    @Unique
    private int rejuvenatingShotLevel;

    @Override
    public void ascendant_arcana$setArchersGambitLevel(int value) {
        this.archersGambitLevel = value;
    }

    @Override
    public void ascendant_arcana$setEvokersWrathLevel(int value) {
        this.evokersWrathLevel = value;
    }

    @Override
    public void ascendant_arcana$setRejuvenatingShotLevel(int value) {
        this.rejuvenatingShotLevel = value;
    }

    @Override
    public void ascendant_arcana$setRicochetLevel(int value) {
        this.ricochetLevel = value;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomAttributes(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("archersGambitLevel", archersGambitLevel);
        nbt.putInt("evokersWrathLevel", evokersWrathLevel);
        nbt.putInt("rejuvenatingShotLevel", rejuvenatingShotLevel);
        nbt.putInt("ricochetLevel", ricochetLevel);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.archersGambitLevel = nbt.getInt("archersGambitLevel");
        this.evokersWrathLevel = nbt.getInt("evokersWrathLevel");
        this.rejuvenatingShotLevel = nbt.getInt("rejuvenatingShotLevel");
        this.ricochetLevel = nbt.getInt("ricochetLevel");
    }

    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), cancellable = true)
    private void healInsteadOfDamage(EntityHitResult entityHitResult, CallbackInfo ci, @Local(ordinal = 0) Entity target, @Local(ordinal = 1) Entity owner, @Local(ordinal = 0) int amount) {
        if (rejuvenatingShotLevel < 1) return;
        PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity) (Object) this;
        if (target instanceof LivingEntity livingTarget) {
            if (target == owner) return;
            livingTarget.heal((float) amount / 2);
            onHit(livingTarget);
            if (!persistentProjectileEntity.getWorld().isClient()) {
                if (getPierceLevel() <= 0) livingTarget.setStuckArrowCount(livingTarget.getStuckArrowCount() + 1);
                for(int i = 0; i < 5; ++i) {
                    double d = livingTarget.getRandom().nextGaussian() * 0.02;
                    double e = livingTarget.getRandom().nextGaussian() * 0.02;
                    double f = livingTarget.getRandom().nextGaussian() * 0.02;
                    livingTarget.getWorld().addImportantParticle(ParticleTypes.HEART, livingTarget.offsetX(2 * livingTarget.getRandom().nextDouble() - 1), livingTarget.getRandomBodyY() + (double)1.0F, livingTarget.offsetZ(2 * livingTarget.getRandom().nextDouble() - 1), d, e, f);
                }
            }
            if (livingTarget instanceof PlayerEntity && owner instanceof ServerPlayerEntity && !persistentProjectileEntity.isSilent()) {
                ((ServerPlayerEntity)owner).networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, 0.0F));
            }
        }
        if (getPierceLevel() <= 0) {
            persistentProjectileEntity.discard();
        }
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void willRicochet(CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity)((Object)this);
        if (projectile.getWorld().isClient()) return;

        Vec3d vel = projectile.getVelocity();
        Vec3d pos = projectile.getPos();
        Vec3d futurePos = pos.add(vel);
        BlockHitResult hitResult = projectile.getWorld().raycast(new RaycastContext(pos, futurePos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, projectile));

        if (hitResult.getType() == HitResult.Type.MISS) return;

        if (ricochetLevel >= 1 && ricochetBounces < ricochetLevel) {
            // Velocity reflection
            Vec3i tempNormal = hitResult.getSide().getVector();
            Vec3d normal = new Vec3d(tempNormal.getX(), tempNormal.getY(), tempNormal.getZ()).normalize();
            double dotProduct = vel.dotProduct(normal);

            ricochetVector = vel.subtract(normal.multiply(2D * dotProduct)).normalize();
            ricochet = true;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void doRicochet(CallbackInfo ci) {
        if (ricochet) {
            // Undo the effects of onBlockHit
            PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity)(Object)this;
            persistentProjectileEntity.shake = 0;
            inGround = false;
            setCritical(true);
            inBlockState = null;

            doRicochet();
        }
    }

    @Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean modifyDamageDealt(Entity instance, DamageSource source, float amount) {
        if (ricochetLevel >= 1 && ricochetBounces == 0) {
            amount /= 2;
        }
        else if (ricochetLevel >= 1 && ricochetBounces > 0) {
            amount += ricochetBounces * 2;
        }
        return instance.damage(source, amount);
    }

    @Inject(method = "onEntityHit", at = @At("TAIL"))
    private void onEntityHitTail(EntityHitResult entityHitResult, CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity)((Object)this);
        LivingEntity owner = (LivingEntity)projectile.getOwner();
        World world = entityHitResult.getEntity().getWorld();

        if (evokersWrathLevel >= 1) {
            summonEvokersWrathFangs(owner, projectile, entityHitResult.getPos(), world);
        }

        if (archersGambitLevel >= 1 && (entityHitResult.getEntity() instanceof LivingEntity) && owner != null) {
            StatusEffectInstance archersGambitInstance = owner.getStatusEffect(AArcanaStatusEffects.ARCHERS_GAMBIT);
            int consecutiveShots = MathHelper.clamp(archersGambitInstance != null ? archersGambitInstance.getAmplifier() + 1 : 0, 0, 2);
            StatusEffectInstance newInstance = new StatusEffectInstance(
                    AArcanaStatusEffects.ARCHERS_GAMBIT,
                    40 * archersGambitLevel,
                    consecutiveShots,
                    false,
                    false,
                    true
            );
            owner.addStatusEffect(newInstance);
        }
    }

    @Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;discard()V"))
    private void ricochetOnEntityHit(PersistentProjectileEntity persistentProjectileEntity, @Local(argsOnly = true) EntityHitResult entityHitResult) {
        if (ricochetLevel >= 1 && ricochetBounces < ricochetLevel && getPierceLevel() == 0) {
            if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                // Removing the stuck arrow applied by the hit
                livingEntity.setStuckArrowCount(livingEntity.getStuckArrowCount() - 1);
            }
            ricochetVector = persistentProjectileEntity.getVelocity().multiply(-0.8D, 1D, -0.8D);
            doRicochet();
        }
        else {
            persistentProjectileEntity.discard();
        }
    }

    @Inject(method = "onBlockHit", at = @At("TAIL"))
    private void onBlockHitTail(BlockHitResult blockHitResult, CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity)((Object)this);
        LivingEntity owner = (LivingEntity)projectile.getOwner();
        World world = projectile.getWorld();
        if (evokersWrathLevel >= 1) {
            summonEvokersWrathFangs(owner, projectile, blockHitResult.getPos(), world);
        }

        if (archersGambitLevel >= 1) {
            if (owner != null && owner.getStatusEffect(AArcanaStatusEffects.ARCHERS_GAMBIT) != null) {
                owner.removeStatusEffect(AArcanaStatusEffects.ARCHERS_GAMBIT);
            }
        }
    }

    @Unique
    private void summonEvokersWrathFangs(LivingEntity owner, PersistentProjectileEntity projectile, Vec3d pos, World world) {
        if (evokersWrathLevel >= 1) {
            BlockPos blockPos = BlockPos.ofFloored(pos);
            boolean bl = false;
            double d = 0.0D;

            do {
                BlockPos blockPos2 = blockPos.down();
                BlockState blockState = world.getBlockState(blockPos2);
                if (blockState.isSideSolidFullSquare(world, blockPos2, Direction.UP)) {
                    if (!world.isAir(blockPos)) {
                        BlockState blockState2 = world.getBlockState(blockPos);
                        VoxelShape voxelShape = blockState2.getCollisionShape(world, blockPos);
                        if (!voxelShape.isEmpty()) {
                            d = voxelShape.getMax(Direction.Axis.Y);
                        }
                    }

                    bl = true;
                    break;
                }

                blockPos = blockPos.down();
            } while(blockPos.getY() >= world.getBottomY());

            if (bl) {
                Vec3d vec3d = blockPos.toCenterPos();
                world.spawnEntity(new EvokerFangsEntity(world, vec3d.getX(), blockPos.getY() + d, vec3d.getZ(), projectile.getYaw(), 0, owner));
            }
        }
    }

    @Unique
    private void doRicochet() {
        ricochetBounces++;

        PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity)(Object)this;

        // Update velocity
        persistentProjectileEntity.setVelocity(ricochetVector);
        persistentProjectileEntity.speed -= 0.5F;
        persistentProjectileEntity.velocityModified = true;
        persistentProjectileEntity.velocityDirty = true;
        // We take an extra step to get out of the block onBlockHit lodged us in
        persistentProjectileEntity.move(MovementType.SELF, ricochetVector.multiply(0.1));

        ricochet = false;
        ricochetVector = null;
    }
}
