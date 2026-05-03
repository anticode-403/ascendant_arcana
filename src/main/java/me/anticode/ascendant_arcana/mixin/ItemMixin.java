package me.anticode.ascendant_arcana.mixin;

import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "usageTick", at = @At("HEAD"))
    private void cleanseShield(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci) {
        if (!((Object) this instanceof ShieldItem) || !(user instanceof PlayerEntity player)) return;
        if (EnchantmentHelper.getLevel(AArcanaEnchantments.CLEANSE, stack) <= 0) return;
        int usageTicks = 72000 - remainingUseTicks;
        if (usageTicks >= 20 && !user.getStatusEffects().isEmpty()) {
            user.clearStatusEffects();
            player.stopUsingItem();
            player.getItemCooldownManager().set(stack.getItem(), 200);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, player.getSoundCategory(), 0.7F, 2);
        }
    }
}
