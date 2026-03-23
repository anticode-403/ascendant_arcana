package me.anticode.ascendant_arcana.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.enchantment.TickableAttributeEnchantment;
import me.anticode.ascendant_arcana.enchantment.TurtleHeart;
import me.anticode.ascendant_arcana.init.AArcanaAttributes;
import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import me.anticode.ascendant_arcana.logic.ItemUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract double getAttributeValue(EntityAttribute attribute);

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    public abstract AttributeContainer getAttributes();

    @Unique
    private Map<AArcanaEnchantments.IndirectHeartDamageTypes, Integer> heartAttackers = new EnumMap<>(AArcanaEnchantments.IndirectHeartDamageTypes.class);

    @Unique
    private final Collection<Pair<EquipmentSlot, ItemStack>> attributeStacks = Lists.newArrayList();


    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static DefaultAttributeContainer.Builder createLivingAttributes(DefaultAttributeContainer.Builder original) {
        original.add(AArcanaAttributes.PROTECTION);
        original.add(AArcanaAttributes.DAMAGE_TAKEN);
        return original;
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
        if (source.getAttacker() != null && source.getAttacker() instanceof LivingEntity) {
            if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.NETHER_HEART, (LivingEntity) source.getAttacker()) > 0)
                heartAttackers.put(AArcanaEnchantments.IndirectHeartDamageTypes.NETHER, 0);
            else if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.COLDHEART, (LivingEntity) source.getAttacker()) > 0)
                heartAttackers.put(AArcanaEnchantments.IndirectHeartDamageTypes.COLD, 0);
            else if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.STORM_HEART, (LivingEntity) source.getAttacker()) > 0)
                heartAttackers.put(AArcanaEnchantments.IndirectHeartDamageTypes.STORM, 0);
            else if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.TURTLE_HEART, (LivingEntity) source.getAttacker()) > 0)
                damage *= 0.75F;
            else if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.WITCH_HEART, (LivingEntity) source.getAttacker()) > 0
                    && (source.isOf(DamageTypes.MAGIC) || source.isOf(DamageTypes.INDIRECT_MAGIC)))
                damage *= 1.2F;
            else if (EnchantmentHelper.getEquipmentLevel(AArcanaEnchantments.BLADEHEART, (LivingEntity) source.getAttacker()) > 0
                    && (source.isIn(DamageTypeTags.IS_PROJECTILE) || source.isOf(DamageTypes.PLAYER_ATTACK)))
                damage *= 1.2F;
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

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if(!((LivingEntity)(Object)this).getWorld().isClient()) {
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
                    ItemUtil.forEachEnchantment((en, stack, lvl)-> {
                        if(en instanceof TickableAttributeEnchantment) {
                            ((TickableAttributeEnchantment) en).removeAttributes((LivingEntity)(Object)this, pair.getLeft());
                        }
                        else if (en instanceof TurtleHeart) {
                            ((TurtleHeart) en).removeAttributes((LivingEntity)(Object)this, pair.getLeft());
                        }
                    }, st, true);
                    it.remove();
                }
            }

            for(EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = getEquippedStack(slot);
                if(!stack.isEmpty()) {
                    ItemUtil.forEachEnchantment((en, st, lvl)-> {
                        if(en instanceof TickableAttributeEnchantment) {
                            ((TickableAttributeEnchantment) en).onTick((LivingEntity)(Object)this, st, lvl);
                            if(missingAttributeStack(st) && ((TickableAttributeEnchantment) en).addAttributes((LivingEntity)(Object)this, st, slot, lvl)) {
                                attributeStacks.add(new Pair<>(slot, st));
                            }
                        }
                        else if (en instanceof TurtleHeart) {
                            if(missingAttributeStack(st) && ((TurtleHeart) en).addAttributes((LivingEntity)(Object)this, st, slot, lvl)) {
                                attributeStacks.add(new Pair<>(slot, st));
                            }
                        }
                    }, stack, false);
                }
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
