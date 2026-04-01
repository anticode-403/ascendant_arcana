package me.anticode.ascendant_arcana.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import me.anticode.ascendant_arcana.recipe.EnchantmentRecipe;
import me.anticode.ascendant_arcana.screenhandler.AArcanaEnchantingScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AArcanaEnchantingScreen extends HandledScreen<AArcanaEnchantingScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(AscendantArcana.modID, "textures/gui/container/enchanting_table.png");
    private static final Identifier OVERLAYS = new Identifier(AscendantArcana.modID, "textures/gui/container/enchanting_table_elements.png");
    private final Random random = Random.createLocal();
    List<EnchantmentRecipe> recipes = new ArrayList<>();
    private List<EnchantmentTile> enchantments = new ArrayList<>();
    private float scrollPosition;
    private boolean scrollerClicked;
    private int visibleTopRow;
    private ItemStack lastItem;

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
                    if (text.getString().length() > 20) text = Text.literal(text.asTruncatedString(17)).append("...");
                    int color = 5636095;
                    if (strength == enchantment.getMaxLevel()) color = 16755200;
                    if (enchantment.isCursed()) color = 16733525;
                    context.getMatrices().push();
                    context.getMatrices().peek().getPositionMatrix().scale(0.5F, 0.5F, 0.5F);
                    int scaledX = (x + 9) * 2;
                    int scaledY = (y + 50 + (i * 5)) * 2;
                    this.textRenderer.draw(text, scaledX, scaledY, color, true, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
                    context.getMatrices().pop();
                    i++;
                }
            }
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        ItemStack itemStack = getScreenHandler().getSlot(0).getStack();
        if (lastItem != null && lastItem != itemStack) {
            recipes = new ArrayList<>();
            for (EnchantmentRecipe recipe : client.world.getRecipeManager().listAllOfType(AArcanaRecipes.ENCHANTMENT_RECIPE_TYPE)) {
                if (recipe.enchantment.isAcceptableItem(itemStack)) {
                    recipes.add(recipe);
                }
            }
        }
        lastItem = itemStack;

        int k = (int)(41.0F * this.scrollPosition);
        boolean hasRecipes = recipes != null && !recipes.isEmpty();
        context.drawTexture(OVERLAYS, 153, 9 + k, (hasRecipes && recipes.size() > 6 ? 0 : 6), 0, 6, 27);

        ItemStack stack = getScreenHandler().getSlot(0).getStack();
        if (stack != ItemStack.EMPTY) {

            int maxCapacity = AArcanaEnchantmentHelper.getEnchantmentCapacity(stack);
            int usedCapacity = AArcanaEnchantmentHelper.getEnchantmentUsage(stack);
            float multiplier = (float) usedCapacity / maxCapacity;
            if (multiplier > 1) multiplier = 1;

            context.drawTexture(OVERLAYS, 8, 110, 25, 0, MathHelper.floor(58 * multiplier), 5);

            if (hasRecipes) {
                int i = 0;
                for (EnchantmentRecipe recipe : recipes) {
                    addEnchantment(recipe, x + 68, y + 8 + (i * 18));
                    i++;
                }
            } else clearEnchantments();
        } else clearEnchantments();
    }

    public void addEnchantment(EnchantmentRecipe recipe, int buttonX, int buttonY) {
        EnchantmentTile tile = new EnchantmentTile(recipe, buttonX, buttonY);
        enchantments.add(tile);
        addDrawableChild(tile);
    }

    public void clearEnchantments() {
        for (EnchantmentTile tile : enchantments) {
            remove(tile);
        }
        enchantments.clear();
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);
        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        scrollerClicked = false;
        if (recipes != null && recipes.size() > 6) {
            int i = x + 60;
            int j = y + 13;

            for(int k = 0; k < 4; ++k) {
                for(int l = 0; l < 4; ++l) {
                    double d = mouseX - (double)(i + l * 14);
                    double e = mouseY - (double)(j + k * 14);
                    int m = k + visibleTopRow;
                    int n = m * 4 + l;
                    if (d >= (double)0.0F && e >= (double)0.0F && d < (double)14.0F && e < (double)14.0F && (getScreenHandler()).onButtonClick(client.player, n)) {
                        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
                        this.client.interactionManager.clickButton((getScreenHandler()).syncId, n);
                        return true;
                    }
                }
            }

            i = this.x + 153;
            j = this.y + 9;
            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 56)) {
                this.scrollerClicked = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrollerClicked && recipes != null && !recipes.isEmpty()) {
            int j = this.y + 9;
            int k = j + 121;
            this.scrollPosition = ((float)mouseY - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
            this.visibleTopRow = Math.max((int)((double)(this.scrollPosition * (float)-recipes.size()) + (double)0.5F), 0);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (recipes != null && !recipes.isEmpty()) {
            int recipeSize = recipes.size();
            float f = (float)amount / (float)-recipeSize;
            this.scrollPosition = MathHelper.clamp(this.scrollPosition - f, 0.0F, 1.0F);
            this.visibleTopRow = Math.max((int)(this.scrollPosition * (float)-recipeSize + 0.5F), 0);
        }

        return true;
    }

    private class EnchantmentTile extends PressableWidget {
        private final EnchantmentRecipe recipe;
        protected boolean selected = false;
        protected boolean locked = false;
        protected boolean maxLevel = false;

        public EnchantmentTile(EnchantmentRecipe recipe, int x, int y) {
            super(x, y, 0, 27, Text.translatable(recipe.enchantment.getTranslationKey()));
            this.recipe = recipe;
            this.width = 84;
            this.height = 18;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int v = 27;
            if (this.selected) v += 54;
            if (this.locked) v += 18;
            else if (this.maxLevel) v += 36;

            context.drawTexture(OVERLAYS, getX(), getY(), 0, v, width, height);
        }

        @Override
        public void onPress() {

        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }
}
