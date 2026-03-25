package me.anticode.ascendant_arcana.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.api.EnchantedArrow;
import me.anticode.ascendant_arcana.init.AArcanaStatusEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin implements EnchantedArrow {
    @Shadow
    protected abstract void onHit(LivingEntity target);

    @Shadow
    public abstract byte getPierceLevel();

    @Unique
    private int archersGambitLevel;

    @Unique
    private int evokersWrathLevel;

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

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomAttributes(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("archersGambitLevel", archersGambitLevel);
        nbt.putInt("evokersWrathLevel", evokersWrathLevel);
        nbt.putInt("rejuvenatingShotLevel", rejuvenatingShotLevel);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.archersGambitLevel = nbt.getInt("archersGambitLevel");
        this.evokersWrathLevel = nbt.getInt("evokersWrathLevel");
        this.rejuvenatingShotLevel = nbt.getInt("rejuvenatingShotLevel");
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
