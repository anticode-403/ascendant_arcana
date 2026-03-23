package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.api.EnchantedArrow;
import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public class BowItemMixin {
    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile" +
            "/PersistentProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V", shift = At.Shift.AFTER))
    private void applyEnchantmentValues(
            ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
            CallbackInfo ci, @Local PersistentProjectileEntity persistentProjectileEntity, @Local float f) {
        int archersGambitLevel = EnchantmentHelper.getLevel(AArcanaEnchantments.ARCHERS_GAMBIT, stack);
        int evokersWrathLevel = EnchantmentHelper.getLevel(AArcanaEnchantments.EVOKERS_WRATH, stack);
        EnchantedArrow enchantedArrow = (EnchantedArrow) persistentProjectileEntity;
        enchantedArrow.ascendant_arcana$setArchersGambitLevel(archersGambitLevel);
        enchantedArrow.ascendant_arcana$setEvokersWrathLevel(evokersWrathLevel);

//        Random random = Random.createLocal();
//        int inaccuracy = EnchantmentHelper.getLevel(AArcanaEnchantments.INACCURACY_CURSE, stack);
//        float rand_pitch = random.nextFloat() * inaccuracy * 2f;
//        float rand_yaw = random.nextFloat() * inaccuracy * 2f;
//        float pitch = user.getPitch() + (random.nextBoolean() ? rand_pitch : -rand_pitch);
//        float yaw = user.getYaw() + (random.nextBoolean() ? rand_yaw : -rand_yaw);
//        persistentProjectileEntity.setVelocity(user, pitch, yaw, 0.0f, f * 3.0f, 1.0f);
    }
}
