package me.anticode.ascendant_arcana.mixin;


import me.anticode.ascendant_arcana.api.EnchantedArrow;
import me.anticode.ascendant_arcana.init.AArcanaStatusEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    @Override
    public void ascendant_arcana$setArchersGambitLevel(int value) {
        this.archersGambitLevel = value;
    }

    @Override
    public void ascendant_arcana$setEvokersWrathLevel(int value) {
        this.evokersWrathLevel = value;
    }

    @Override
    public void ascendant_arcana$setRicochetLevel(int value) {
        this.ricochetLevel = value;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomAttributes(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("archersGambitLevel", archersGambitLevel);
        nbt.putInt("evokersWrathLevel", evokersWrathLevel);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.archersGambitLevel = nbt.getInt("archersGambitLevel");
        this.evokersWrathLevel = nbt.getInt("evokersWrathLevel");
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

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (ricochet) {
            // Undo the effects of onBlockHit
            PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity)(Object)this;
            persistentProjectileEntity.shake = 0;
            inGround = false;
            setCritical(true);
            inBlockState = null;

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

    @Inject(method = "onBlockHit", at = @At("HEAD"), cancellable = true)
    private void onBlockHitHead(BlockHitResult blockHitResult, CallbackInfo ci) {
        ProjectileEntity projectileEntity = (ProjectileEntity)(Object)this;
        if (projectileEntity.getWorld().isClient()) return;
        if (ricochetLevel >= 1 && ricochetBounces < ricochetLevel) {
            ricochetBounces++;

            // Velocity reflection
            Vec3d oldVel = projectileEntity.getVelocity();
            Vec3i tempNormal = blockHitResult.getSide().getVector();
            Vec3d normal = new Vec3d(tempNormal.getX(), tempNormal.getY(), tempNormal.getZ()).normalize();
            double dotProduct = oldVel.dotProduct(normal);

            ricochetVector = oldVel.subtract(normal.multiply(2D * dotProduct)).normalize();
            ricochet = true;
            ci.cancel();
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
}
