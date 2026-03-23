package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.api.EnchantedTrident;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.NbtCompound;
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

    @Override
    public void ascendant_arcana$setAmbushLevel(int value) {
        this.ambushLevel = value;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomAttributes(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("ambushLevel", ambushLevel);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.ambushLevel = nbt.getInt("ambushLevel");
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
}