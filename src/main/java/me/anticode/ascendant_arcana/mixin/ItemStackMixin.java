package me.anticode.ascendant_arcana.mixin;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.init.AArcanaAttributes;
import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import me.anticode.ascendant_arcana.logic.ItemHelper;
import me.anticode.ascendant_arcana.logic.RelicHelper;
import me.anticode.ascendant_arcana.logic.Relics;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract NbtCompound getOrCreateNbt();

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract NbtList getEnchantments();

    @Unique
    private static Enchantment replacement = null;

    @Inject(method = "addEnchantment", at = @At("HEAD"), cancellable = true)
    private void enchantmentCapacity(Enchantment enchantment, int level, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if (!AArcanaEnchantmentHelper.testEnchantmentCost(stack, AArcanaEnchantmentHelper.getEnchantmentCost(enchantment, level))) {
            ci.cancel();
        }
    }

    @Inject(method = "addEnchantment", at = @At("HEAD"), cancellable = true)
    private void disableEnchantments(Enchantment enchantment, int level, CallbackInfo ci) {
        if (!AArcanaEnchantmentHelper.isEnchantmentEnabled(enchantment)) {
            replacement = AArcanaEnchantmentHelper.getReplacement(enchantment, (ItemStack) (Object) this);
            if (replacement == null) {
                ci.cancel();
            }
        }
    }

    @ModifyVariable(method = "addEnchantment", at = @At("HEAD"), argsOnly = true)
    private Enchantment disableEnchantments(Enchantment value) {
        if (replacement != null) {
            return replacement;
        }
        return value;
    }

    @ModifyVariable(method = "addEnchantment", at = @At("HEAD"), argsOnly = true)
    private int disableEnchantments(int value) {
        if (replacement != null) {
            Enchantment temp = replacement;
            replacement = null;
            return Math.min(temp.getMaxLevel(), value);
        }
        return value;
    }

    @ModifyReturnValue(method = "getMaxDamage", at = @At("RETURN"))
    private int implementDurabilityRelic(int maxDamage) {
        return maxDamage + RelicHelper.getTooltipStrength(Relics.DURABILITY, RelicHelper.getValueFromNbt(getOrCreateNbt(), Relics.DURABILITY));
    }

    @ModifyReturnValue(method = "getAttributeModifiers", at = @At("RETURN"))
    private Multimap<EntityAttribute, EntityAttributeModifier> implementAttributeRelics(Multimap<EntityAttribute, EntityAttributeModifier> original, @Local(argsOnly = true) EquipmentSlot slot) {
        if (getItem() instanceof ArmorItem armorItem) {
            if (slot != armorItem.getSlotType()) return original;
            UUID uuid = switch (armorItem.getSlotType()) {
                case EquipmentSlot.HEAD -> UUID.fromString("ccd7386d-62cf-4ef7-8cc1-a8a2ac7f942c");
                case EquipmentSlot.CHEST -> UUID.fromString("610c3b9b-9c45-4845-8289-99dbe5034894");
                case EquipmentSlot.LEGS -> UUID.fromString("e91f5ebf-3c02-43ec-a842-ce9b68a80c3a");
                case EquipmentSlot.FEET -> UUID.fromString("93ef9100-4f32-45e0-8568-f837918e9b43");
                default -> null;
            };
            int protectionValue = RelicHelper.getTooltipStrength(Relics.PROTECTION, RelicHelper.getValueFromNbt(getOrCreateNbt(), Relics.PROTECTION));
            if (protectionValue != 0) {
                EntityAttributeModifier modifier = new EntityAttributeModifier(uuid, "Protection Relic Bonus", protectionValue * 0.01, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                original.put(AArcanaAttributes.PROTECTION, modifier);
            }
        }
        else if (getItem() instanceof ToolItem) {
            if (slot != EquipmentSlot.MAINHAND) return original;
            Map<Relics, Integer> relics = RelicHelper.fromNbt(getOrCreateNbt());
            if (relics.isEmpty()) return original;
            if (relics.containsKey(Relics.DAMAGE)) {
                double damageValue = RelicHelper.getTooltipStrength(Relics.DAMAGE, relics.get(Relics.DAMAGE))*0.01;
                List<EntityAttributeModifier> oldDamageModifiers = original.get(EntityAttributes.GENERIC_ATTACK_DAMAGE).stream().toList();
                List<EntityAttributeModifier> newModifiers = ItemHelper.multiplyAttributeList(oldDamageModifiers, damageValue);
                original.replaceValues(EntityAttributes.GENERIC_ATTACK_DAMAGE, newModifiers);
            }
        }
        else if (getItem() instanceof CrossbowItem || getItem() instanceof BowItem) {
            if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND) return original;
            Map<Relics, Integer> relics = RelicHelper.fromNbt(getOrCreateNbt());
            if (relics.isEmpty()) return original;
            if (relics.containsKey(Relics.DAMAGE)) {
                double damageValue = RelicHelper.getTooltipStrength(Relics.DAMAGE, relics.get(Relics.DAMAGE)) * 0.01;
                List<EntityAttributeModifier> oldDamageModifiers = original.get(EntityAttributes_RangedWeapon.DAMAGE.attribute).stream().toList();
                List<EntityAttributeModifier> newModifiers = ItemHelper.multiplyAttributeList(oldDamageModifiers, damageValue);
                original.replaceValues(EntityAttributes_RangedWeapon.DAMAGE.attribute, newModifiers);
            }
            if (relics.containsKey(Relics.HASTE)) {
                double hasteValue = RelicHelper.getTooltipStrength(Relics.HASTE, relics.get(Relics.HASTE)) * 0.01;
                EntityAttributeModifier modifier = new EntityAttributeModifier(UUID.fromString("f2bb3e62-513f-4804-a194-2965d232c7ad"), "Haste Relic Bonus", hasteValue, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                original.put(EntityAttributes_RangedWeapon.HASTE.attribute, modifier);
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "getMiningSpeedMultiplier", at = @At("RETURN"))
    private float applySwiftnessMiningSpeedBonus(float miningSpeedMultiplier) {
        Map<Relics, Integer> relics = RelicHelper.fromNbt(getOrCreateNbt());
        if (!relics.containsKey(Relics.HASTE)) return miningSpeedMultiplier;
        float hasteValue = (float)RelicHelper.getTooltipStrength(Relics.HASTE, relics.get(Relics.HASTE));
        return miningSpeedMultiplier *  (1 + (hasteValue * 0.01F));
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isSectionVisible(ILnet/minecraft/item/ItemStack$TooltipSection;)Z", ordinal = 1))
    private void addRelicTooltipInfo(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, @Local List<Text> tooltip) {
        Map<Relics, Integer> relics = RelicHelper.fromNbt(getOrCreateNbt());
        if (relics.isEmpty()) return;
        tooltip.add(Text.empty());
        tooltip.add(Text.translatable("item.relics.tooltip.on_tool", relics.size(), RelicHelper.getRelicCapacity((ItemStack)(Object)this)).formatted(Formatting.GRAY));
        for (Map.Entry<Relics, Integer> entry : relics.entrySet()) {
            int visualStrength = RelicHelper.getTooltipStrength(entry.getKey(), entry.getValue());
            Text relicName = Text.translatable("item.relics.type." + entry.getKey().toString().toLowerCase());
            String hasPercent = (entry.getKey() == Relics.HASTE || entry.getKey() == Relics.PROTECTION || entry.getKey() == Relics.DAMAGE) ? "%" : "";
            Text line = Text.translatable("item.relics.tooltip", visualStrength, relicName, hasPercent).formatted(Formatting.BLUE);
            tooltip.add(line);
        }
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private Multimap<EntityAttribute, EntityAttributeModifier> removeProtectionAttributeFromTooltip(ItemStack instance, EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = instance.getAttributeModifiers(slot);
        modifiers.removeAll(AArcanaAttributes.PROTECTION);
        return modifiers;
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getDamage()I", shift = At.Shift.AFTER))
    private void addEnchantmentCapacityTooltipWhileAdvanced(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, @Local List<Text> tooltip) {
        if (context.isAdvanced()) {
            ItemStack itemStack = (ItemStack)(Object)this;
            tooltip.add(Text.translatable("item.enchantment_capacity", AArcanaEnchantmentHelper.getEnchantmentUsage(itemStack), AArcanaEnchantmentHelper.getEnchantmentCapacity(itemStack)));
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "TAIL"))
    private void addTreasureEnchantmentInfo(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, @Local List<Text> tooltip) {
        if (getItem() instanceof EnchantedBookItem) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get((ItemStack)(Object) this);
            boolean hasTreasure = false;
            for (Enchantment enchantment : enchantments.keySet()) {
                if (enchantment.isTreasure()) hasTreasure = true;
            }
            if (hasTreasure) {
                tooltip.add(Text.translatable("item.book_contains_treasure_title").formatted(Formatting.GOLD));
                tooltip.add(Text.translatable("item.book_contains_treasure_body").formatted(Formatting.GOLD));
            }
        }
    }
}
