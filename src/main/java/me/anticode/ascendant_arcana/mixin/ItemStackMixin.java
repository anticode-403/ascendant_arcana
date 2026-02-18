package me.anticode.ascendant_arcana.mixin;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.init.AArcanaAttributes;
import me.anticode.ascendant_arcana.logic.RelicHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract NbtCompound getOrCreateNbt();

    @Shadow
    public abstract Item getItem();

    @ModifyReturnValue(method = "getMaxDamage", at = @At("RETURN"))
    private int implementDurabilityRelic(int maxDamage) {
        return maxDamage + RelicHelper.convertStrengthIntoReal(RelicHelper.Relics.DURABILITY, RelicHelper.getValueFromNbt((NbtList)getOrCreateNbt().get(RelicHelper.AARELICS_KEY), RelicHelper.Relics.DURABILITY));
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
            int protectionValue = RelicHelper.convertStrengthIntoReal(RelicHelper.Relics.PROTECTION, RelicHelper.getValueFromNbt((NbtList) getOrCreateNbt().get(RelicHelper.AARELICS_KEY), RelicHelper.Relics.PROTECTION));
            if (protectionValue != 0) {
                EntityAttributeModifier modifier = new EntityAttributeModifier(uuid, "Protection Relic Bonus", protectionValue * 0.01, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                original.put(AArcanaAttributes.PROTECTION, modifier);
            }
        }
        if (getItem() instanceof ToolItem) {
            if (slot != EquipmentSlot.MAINHAND) return original;
            Map<RelicHelper.Relics, Integer> relics = RelicHelper.fromNbt((NbtList)getOrCreateNbt().get(RelicHelper.AARELICS_KEY));
            if (relics.isEmpty()) return original;
            UUID uuid = UUID.fromString("4363a989-2822-4c1a-b74a-07493b9b30a4");
            if (relics.containsKey(RelicHelper.Relics.HASTE)) {
                int hasteValue = RelicHelper.convertStrengthIntoReal(RelicHelper.Relics.HASTE, relics.get(RelicHelper.Relics.HASTE));
                EntityAttributeModifier modifier = new EntityAttributeModifier(uuid, "Haste Relic Bonus", hasteValue * 0.01, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                original.put(AArcanaAttributes.SWIFTNESS, modifier);
            }
            if (relics.containsKey(RelicHelper.Relics.DAMAGE)) {
                int damageValue = RelicHelper.convertStrengthIntoReal(RelicHelper.Relics.DAMAGE, relics.get(RelicHelper.Relics.DAMAGE));
                List<EntityAttributeModifier> oldDamageModifiers = original.get(EntityAttributes.GENERIC_ATTACK_DAMAGE).stream().toList();
                List<EntityAttributeModifier> newModifiers = new LinkedList<>();
                for (EntityAttributeModifier mod : oldDamageModifiers) {
                    double newValue = mod.getValue() * (1 + (damageValue*0.01));
                    EntityAttributeModifier newMod = new EntityAttributeModifier(mod.getId(), mod.getName(), newValue, mod.getOperation());
                    newModifiers.add(newMod);
                }
                original.replaceValues(EntityAttributes.GENERIC_ATTACK_DAMAGE, newModifiers);
            }
        }
        return original;
    }
}
