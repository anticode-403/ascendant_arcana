package me.anticode.ascendant_arcana.enchantment;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class DepthsCurse extends TickableAttributeEnchantment{
    public DepthsCurse() {
        super(true, Rarity.VERY_RARE, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    @Override
    public void onTick(LivingEntity entity, ItemStack stack, int level, EquipmentSlot slot)
    {
        if (!slot.isArmorSlot()) return;
        if(entity instanceof PlayerEntity player)
            if(player.getAbilities().flying)
                return;

        if(entity.isInsideWaterOrBubbleColumn())
        {
            Vec3d vel = entity.getVelocity();
            if(vel.y > -1F)
            {
                double max = 0.05 * level;
                double yV = Math.max(-max, vel.y - max);
                entity.setVelocity(vel.x, yV, vel.z);
                entity.velocityDirty = true;
                entity.velocityModified = true;
            }
        }
    }
}