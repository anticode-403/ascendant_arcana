package me.anticode.ascendant_arcana.client.mixin;

import me.anticode.ascendant_arcana.AscendantArcana;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    protected abstract void drawHeart(DrawContext drawContext, InGameHud.HeartType heartType, int x, int y, int v, boolean blinking, boolean halfHeart);

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract void render(DrawContext context, float tickDelta);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasExperienceBar()Z"))
    private boolean doesNotHaveXpBar(ClientPlayerInteractionManager manager) {
        if (AscendantArcana.config.disable_xp) return false;
        return manager.hasExperienceBar();
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void doesNotHaveXpBar(DrawContext context, int x, CallbackInfo ci) {
        if (AscendantArcana.config.disable_xp) ci.cancel();
    }

    @Redirect(method = "renderHealthBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIIZZ)V"))
    void moveHealthBarDown(InGameHud instance, DrawContext context, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart) {
        if (!AscendantArcana.config.disable_xp || (client.player != null && client.player.getJumpingMount() != null)) {
            drawHeart(context, type, x, y, v, blinking, halfHeart);
            return;
        }

        drawHeart(context, type, x, y + 7, v, blinking, halfHeart);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"))
    void moveHungerBarDown(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height) {
        if (!AscendantArcana.config.disable_xp || (client.player != null && client.player.getJumpingMount() != null)) {
            instance.drawTexture(texture, x, y, u, v, width, height);
            return;
        }

        instance.drawTexture(texture, x, y + 7, u, v, width, height);
    }

    @Redirect(method = "renderMountHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"))
    void moveAirBarDown(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height) {
        if (!AscendantArcana.config.disable_xp || (client.player != null && client.player.getJumpingMount() != null)) {
            instance.drawTexture(texture, x, y, u, v, width, height);
            return;
        }

        instance.drawTexture(texture, x, y+7, u, v, width, height);
    }
}
