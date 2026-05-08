package me.anticode.ascendant_arcana.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.enchantment.HellWalker;
import me.anticode.ascendant_arcana.enchantment.TickableAttributeEnchantment;
import me.anticode.ascendant_arcana.enchantment.TurtleHeart;
import me.anticode.ascendant_arcana.init.AArcanaAttributes;
import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import me.anticode.ascendant_arcana.init.AArcanaStatusEffects;
import me.anticode.ascendant_arcana.logic.ItemHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract double getAttributeValue(EntityAttribute attribute);

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    public abstract AttributeContainer getAttributes();

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Shadow
    @Nullable
    public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow
    public abstract void setStatusEffect(StatusEffectInstance effect, @Nullable Entity source);

    @Shadow
    public abstract boolean removeStatusEffect(StatusEffect type);

    @Shadow
    public abstract int getItemUseTime();

    @Shadow
    public abstract boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source);

    @Unique
    private Map<AArcanaEnchantments.IndirectHeartDamageTypes, Integer> heartAttackers = new EnumMap<>(AArcanaEnchantments.IndirectHeartDamageTypes.class);

    @Unique
    private final Collection<Pair<EquipmentSlot, ItemStack>> attributeStacks = Lists.newArrayList();

    @Inject(method = "onAttacking", at = @At("HEAD"))
    private void removeCrossCounterOnAttack(Entity target, CallbackInfo ci) {
        if (getStatusEffect(AArcanaStatusEffects.CROSS_COUNTER) != null) {
            removeStatusEffect(AArcanaStatusEffects.CROSS_COUNTER);
        }
    }

    @Inject(method = "takeShieldHit", at = @At("HEAD"))
    private void addCrossCounterOnParry(LivingEntity attacker, CallbackInfo ci) {
        int level = EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.CROSS_COUNTER, (LivingEntity)(Object)this);
        if (level <= 0) return;
        int useTime = getItemUseTime();
        if (useTime <= 0) return;
        if (useTime > 5 + 5 * level) return;
        StatusEffectInstance crossCounter = new StatusEffectInstance(AArcanaStatusEffects.CROSS_COUNTER, 15 * level, 0, false, false, true);
        addStatusEffect(crossCounter, (LivingEntity)(Object)this);
    }

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static DefaultAttributeContainer.Builder createLivingAttributes(DefaultAttributeContainer.Builder original) {
        original.add(AArcanaAttributes.PROTECTION);
        original.add(AArcanaAttributes.DAMAGE_TAKEN);
        return original;
    }

    @ModifyReturnValue(method = "getJumpVelocity", at = @At("RETURN"))
    private float modifyJumpVelocity(float original) {
        StatusEffectInstance hobbled = getStatusEffect(AArcanaStatusEffects.HOBBLED);
        if (hobbled != null) {
            return original * (1F - (0.1F * hobbled.getAmplifier()));
        }
        return original;
    }

    @Inject(method = "applyMovementEffects", at = @At("HEAD"))
    private void applyMovementEffects(BlockPos pos, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.HELLWALKER, livingEntity) > 0) {
            HellWalker.freezeLava(livingEntity, livingEntity.getWorld(), pos);
        }
    }

    @ModifyReturnValue(method = "modifyAppliedDamage", at = @At("RETURN"))
    private float applyProtectionStat(float original, @Local(argsOnly = true) DamageSource source) {
        if (source.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)) return original;
        double protectionStrength = getAttributeValue(AArcanaAttributes.PROTECTION);
        float multiplier = 2F - (float) protectionStrength;
        return original * multiplier;
    }

    @ModifyVariable(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private StatusEffectInstance injectStatusEffectModifiers(StatusEffectInstance effect) {
        StatusEffectInstance newInstance = new StatusEffectInstance(effect);
        if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.ALCHEMISTS_HEART, (LivingEntity) (Object)this) >= 1
                && newInstance.getEffectType().isBeneficial()) {
            newInstance = new StatusEffectInstance(newInstance.getEffectType(), newInstance.getDuration(), newInstance.getAmplifier() + 1, newInstance.isAmbient(), newInstance.shouldShowParticles());
        }
        return newInstance;
    }

    @ModifyReturnValue(method = "modifyAppliedDamage", at = @At("TAIL"))
    private float injectDamageModifiers(float damage, @Local(argsOnly = true) DamageSource source) {
        if (heartAttackers == null) {
            heartAttackers = new EnumMap<>(AArcanaEnchantments.IndirectHeartDamageTypes.class);
        }
        if (source.getAttacker() != null && source.getAttacker() instanceof LivingEntity attacker) {
            if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.NETHER_HEART, attacker) > 0)
                heartAttackers.put(AArcanaEnchantments.IndirectHeartDamageTypes.NETHER, 0);
            else if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.COLDHEART, attacker) > 0)
                heartAttackers.put(AArcanaEnchantments.IndirectHeartDamageTypes.COLD, 0);
            else if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.STORM_HEART, attacker) > 0)
                heartAttackers.put(AArcanaEnchantments.IndirectHeartDamageTypes.STORM, 0);
            else if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.TURTLE_HEART, attacker) > 0)
                damage *= 0.75F;
            else if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.WITCH_HEART, attacker) > 0
                    && (source.isOf(DamageTypes.MAGIC) || source.isOf(DamageTypes.INDIRECT_MAGIC)))
                damage *= 1.2F;
            else if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.BLADEHEART, attacker) > 0
                    && (source.isIn(DamageTypeTags.IS_PROJECTILE) || source.isOf(DamageTypes.PLAYER_ATTACK)))
                damage *= 1.2F;
            if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.PINCUSHION, attacker) > 0) {
                damage *= 0.9F + (0.1F * ((LivingEntity)(Object)this).getStuckArrowCount());
            }
        }

        if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS) || damage >= 1.1342745E38F) return damage;

        double damage_taken = getAttributes().getValue(AArcanaAttributes.DAMAGE_TAKEN);
        damage *= (float) damage_taken;

        if (source.isIn(DamageTypeTags.IS_FIRE)) {
            if (heartAttackers.containsKey(AArcanaEnchantments.IndirectHeartDamageTypes.NETHER)) {
                damage *= 2;
            }
        }
        if (source.isIn(DamageTypeTags.IS_LIGHTNING) && heartAttackers.containsKey(AArcanaEnchantments.IndirectHeartDamageTypes.STORM))
            damage *= 2;
        if (source.isIn(DamageTypeTags.IS_FREEZING) && heartAttackers.containsKey(AArcanaEnchantments.IndirectHeartDamageTypes.COLD))
            damage *= 2;
        return damage;
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;onDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"), cancellable = true)
    private void protectiveEcho(DamageSource source, float amount, CallbackInfo ci) {
        if (amount < 5) return;
        if (getStatusEffect(AArcanaStatusEffects.ECHOING_DAMAGE) != null) return;
        if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.PROTECTIVE_ECHO, (LivingEntity) (Object) this) == 0) return;
        setStatusEffect(new StatusEffectInstance(AArcanaStatusEffects.ECHOING_DAMAGE, 5, (int)Math.floor(amount / 5)), (LivingEntity)(Object)this);
        ci.cancel();
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathEnchantments(DamageSource damageSource, CallbackInfo ci) {
        if (damageSource.getAttacker() instanceof LivingEntity attackingEntity) {
            LivingEntity livingEntity = (LivingEntity) (Object) this;

            int soulBurstLevel = EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.SOUL_BURST, attackingEntity);
            if (soulBurstLevel > 0) {
                float soulBurstDamage = livingEntity.getMaxHealth() * 0.2f * soulBurstLevel;
                float soulBurstRadius = 0.5f * soulBurstDamage;

                AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(livingEntity.getWorld(), livingEntity.getX(), livingEntity.getRandomBodyY(), livingEntity.getZ());
                areaEffectCloudEntity.setOwner(attackingEntity);
                areaEffectCloudEntity.setParticleType(ParticleTypes.SCULK_SOUL);
                areaEffectCloudEntity.setRadius(soulBurstRadius);
                areaEffectCloudEntity.setDuration(0);
                livingEntity.getWorld().spawnEntity(areaEffectCloudEntity);
                livingEntity.getWorld().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.ENTITY_EVOKER_CAST_SPELL, attackingEntity.getSoundCategory(), 1.0F, 1.0F);

                List<LivingEntity> targets = livingEntity.getEntityWorld().getEntitiesByClass(LivingEntity.class, new Box(livingEntity.getBlockPos()).expand(soulBurstRadius), (LivingEntity e) -> {
                    if (e == attackingEntity) return false;
                    else return !(e instanceof TameableEntity tameableEntity) || !tameableEntity.isOwner(attackingEntity);
                });

                for (LivingEntity target : targets) {
                    target.damage(attackingEntity.getDamageSources().explosion(livingEntity, attackingEntity), soulBurstDamage);
                }
            }

            int debilitatingChainLevel = EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.DEBILITATING_CHAIN, attackingEntity);
            if (debilitatingChainLevel > 0) {
                float searchRadius = 2 + 2 * debilitatingChainLevel;

                List<LivingEntity> targets = livingEntity.getEntityWorld().getEntitiesByClass(LivingEntity.class, new Box(livingEntity.getBlockPos()).expand(searchRadius), (LivingEntity e) -> {
                    if (e == attackingEntity) return false;
                    else if (e == livingEntity) return false;
                    else return !(e instanceof TameableEntity tameableEntity) || !tameableEntity.isOwner(attackingEntity);
                });

                LivingEntity target = null;
                for (LivingEntity potentialTarget : targets) {
                    if (target == null) {
                        target = potentialTarget;
                        continue;
                    }
                    if (livingEntity.getPos().distanceTo(potentialTarget.getPos()) < livingEntity.getPos().distanceTo(target.getPos())) target = potentialTarget;
                }

                if (target != null) {
                    for (StatusEffectInstance effect : livingEntity.getStatusEffects()) {
                        target.addStatusEffect(effect, attackingEntity);
                    }
                    if (!livingEntity.getStatusEffects().isEmpty()) {
                        if (!livingEntity.getWorld().isClient()) {
                            for (int i = 0; i < livingEntity.getEyePos().distanceTo(target.getEyePos()) * 4; i++) {
                                double delta = ((double)i) / (livingEntity.getEyePos().distanceTo(target.getEyePos()) * 4);
                                Vec3d particlePos = livingEntity.getEyePos().lerp(target.getEyePos(), delta);
                                ((ServerWorld)attackingEntity.getWorld()).spawnParticles(ParticleTypes.ENCHANTED_HIT, particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
                            }
                            Vec3d soundPos = livingEntity.getPos().lerp(target.getPos(), 0.5D);
                            livingEntity.getWorld().playSound(null, soundPos.getX(), soundPos.getY(), soundPos.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, attackingEntity.getSoundCategory(), 0.8F, 0.1F);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        if(!livingEntity.getWorld().isClient()) {
            if (heartAttackers != null) {
                for (AArcanaEnchantments.IndirectHeartDamageTypes damageType : heartAttackers.keySet()) {
                    if (heartAttackers.get(damageType) > 300) heartAttackers.remove(damageType);
                    else heartAttackers.put(damageType, heartAttackers.get(damageType) + 1);
                }
            } else {
                heartAttackers = new EnumMap<>(AArcanaEnchantments.IndirectHeartDamageTypes.class);
            }
            Iterator<Pair<EquipmentSlot, ItemStack>> it = attributeStacks.iterator();
            while(it.hasNext()) {
                Pair<EquipmentSlot, ItemStack> pair = it.next();
                ItemStack st = pair.getRight();
                if(!hasStackEquipInSlot(st, pair.getLeft())) {
                    ItemHelper.forEachEnchantment((en, stack, lvl)-> {
                        if(en instanceof TickableAttributeEnchantment) {
                            ((TickableAttributeEnchantment) en).removeAttributes(livingEntity, pair.getLeft());
                        }
                        else if (en instanceof TurtleHeart) {
                            ((TurtleHeart) en).removeAttributes(livingEntity, pair.getLeft());
                        }
                    }, st, true);
                    it.remove();
                }
            }

            for(EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = getEquippedStack(slot);
                if(!stack.isEmpty()) {
                    ItemHelper.forEachEnchantment((en, st, lvl)-> {
                        if(en instanceof TickableAttributeEnchantment) {
                            ((TickableAttributeEnchantment) en).onTick(livingEntity, st, lvl);
                            if(missingAttributeStack(st) && ((TickableAttributeEnchantment) en).addAttributes(livingEntity, st, slot, lvl)) {
                                attributeStacks.add(new Pair<>(slot, st));
                            }
                        }
                        else if (en instanceof TurtleHeart) {
                            if(missingAttributeStack(st) && ((TurtleHeart) en).addAttributes(livingEntity, st, slot, lvl)) {
                                attributeStacks.add(new Pair<>(slot, st));
                            }
                        }
                    }, stack, false);
                }
            }

            if (livingEntity.getWorld().getTime() % 20 == 0 && getStatusEffect(AArcanaStatusEffects.ECHOING_DAMAGE) != null) {
                StatusEffectInstance instance = getStatusEffect(AArcanaStatusEffects.ECHOING_DAMAGE);
                int damage = instance.getAmplifier();
                damage(livingEntity.getWorld().getDamageSources().magic(), damage);
            }
        }
    }

    @Unique
    private boolean hasStackEquipInSlot(ItemStack stack, EquipmentSlot slot) {
        return getEquippedStack(slot).equals(stack);
    }

    @Unique
    public boolean missingAttributeStack(ItemStack stack) {
        for(Pair<EquipmentSlot, ItemStack> pair : attributeStacks) {
            if(pair.getRight().equals(stack)) return false;
        }
        return true;
    }
}
