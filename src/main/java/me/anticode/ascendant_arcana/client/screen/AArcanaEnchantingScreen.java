package me.anticode.ascendant_arcana.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import me.anticode.ascendant_arcana.recipe.EnchantmentRecipe;
import me.anticode.ascendant_arcana.screenhandler.AArcanaEnchantingScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.Map;

public class AArcanaEnchantingScreen extends HandledScreen<AArcanaEnchantingScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(AscendantArcana.modID, "textures/gui/container/enchanting_table.png");
    private static final Identifier OVERLAYS = new Identifier(AscendantArcana.modID, "textures/gui/container/enchanting_table_elements.png");
    private final Random random = Random.createLocal();

    public AArcanaEnchantingScreen(AArcanaEnchantingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 229;
        this.backgroundHeight = 218;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        if (getScreenHandler().getSlot(0).hasStack()) {
            ItemStack stack = getScreenHandler().getSlot(0).getStack();
            if (stack.hasEnchantments()) {
                int i = 0;
                for (Map.Entry<Enchantment, Integer> enchantInstance : EnchantmentHelper.get(stack).entrySet()) {
                    Enchantment enchantment = enchantInstance.getKey();
                    int strength = enchantInstance.getValue();
                    MutableText text = Text.translatable(enchantment.getTranslationKey());
                    if (enchantment.getMaxLevel() != 1) text.append(" ").append(Text.translatable("enchantment.level." + strength));
                    context.drawText(this.textRenderer, text, x + 9, y + 50 + (i * 10), 5636095, false);
                    i++;
                }
            }
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        ItemStack stack = getScreenHandler().getSlot(0).getStack();
        if (stack != ItemStack.EMPTY) {
            int maxCapacity = AArcanaEnchantmentHelper.getEnchantmentCapacity(stack);
            int usedCapacity = AArcanaEnchantmentHelper.getEnchantmentUsage(stack);
            float multiplier = (float) usedCapacity / maxCapacity;
            if (multiplier > 1) multiplier = 1;

            context.drawTexture(OVERLAYS, 8, 110, 19, 0, MathHelper.floor(58 * multiplier), 5);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);
        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    private class EnchantmentTile extends PressableWidget {
        private final EnchantmentRecipe recipe;

        public EnchantmentTile(EnchantmentRecipe recipe, int x, int y, int u, int v) {
            super(x, y, u, v, Text.translatable(recipe.enchantment.getTranslationKey()));
            this.recipe = recipe;
        }

        @Override
        public void onPress() {

        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }
}
