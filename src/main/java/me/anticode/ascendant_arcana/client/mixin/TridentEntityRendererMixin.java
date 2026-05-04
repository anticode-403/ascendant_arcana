package me.anticode.ascendant_arcana.client.mixin;

import me.anticode.ascendant_arcana.api.EnchantedTrident;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntityRenderer.class)
public abstract class TridentEntityRendererMixin extends EntityRenderer<TridentEntity> {
    @Shadow
    @Final
    private TridentEntityModel model;

    @Shadow
    public abstract Identifier getTexture(TridentEntity tridentEntity);

    protected TridentEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void stuckTridents(TridentEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        EnchantedTrident enchantedTrident = (EnchantedTrident) entity;
        LivingEntity stuckEntity = enchantedTrident.ascendant_arcana$getStuckEntity();
        if (stuckEntity != null) {
            float offsetX = MathHelper.sin(enchantedTrident.ascendant_arcana$getRenderTicks()), offsetZ = MathHelper.cos(enchantedTrident.ascendant_arcana$getRenderTicks());
            matrices.push();
            matrices.translate(offsetX, 0, offsetZ);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) -MathHelper.wrapDegrees((MathHelper.atan2(stuckEntity.getZ() - entity.getZ() + offsetZ, stuckEntity.getX() - entity.getX() + offsetX) * 57.2957763671875) - 90) + 90));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(60));
            matrices.translate(0, -enchantedTrident.ascendant_arcana$getStabTicks(), 0);
            model.render(matrices, ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, model.getLayer(getTexture(entity)), false, entity.isEnchanted()), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            matrices.pop();
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
            ci.cancel();
        }
    }
}
