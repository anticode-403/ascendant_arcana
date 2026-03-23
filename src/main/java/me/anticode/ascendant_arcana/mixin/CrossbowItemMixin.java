package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.init.AArcanaEnchantments;
import me.anticode.ascendant_arcana.logic.ItemUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CrossbowItem.class, priority = 1500)
public class CrossbowItemMixin {
    @Inject(method = "createArrow", at = @At(value = "RETURN"))
    private static void applyCrossbowEnchantmentLevels(
            World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow,
            CallbackInfoReturnable<PersistentProjectileEntity> cir) {
        if (CrossbowItem.isCharged(crossbow)) {
            ItemUtil.applyPpeRelicsAndEnchantments(cir.getReturnValue(), crossbow);
        }
    }

    @Inject(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;setVelocity(DDDFF)V", shift = At.Shift.AFTER))
    private static void applyCrossbowEnchantmentLevels(
            World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile,
            float soundPitch, boolean creative, float speed, float divergence, float simulated, CallbackInfo ci,
            @Local ProjectileEntity projectileEntity) {
        Random random = Random.createLocal();

        float base_yaw = -projectileEntity.getYaw();
        float base_pitch = -projectileEntity.getPitch();
        int inaccuracy = EnchantmentHelper.getLevel(AArcanaEnchantments.INACCURACY_CURSE, crossbow);
        float rand_pitch = random.nextFloat() * inaccuracy * 2f;
        float rand_yaw = random.nextFloat() * inaccuracy * 2f;
        float pitch = base_pitch + (random.nextBoolean() ? rand_pitch : -rand_pitch);
        float yaw = base_yaw + (random.nextBoolean() ? rand_yaw : -rand_yaw);
        projectileEntity.setVelocity(shooter, pitch, yaw, 0.0f, speed, divergence);
    }
}
