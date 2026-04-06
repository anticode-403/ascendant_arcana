package me.anticode.ascendant_arcana.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.init.AArcanaItems;
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
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
    private int lastBackgroundWidth;
    private int lastBackgroundHeight;
    private int lastPower = 0;
    private int selectedTile;
    private boolean anySelected;

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
        boolean update = false;
        ItemStack itemStack = getScreenHandler().getSlot(0).getStack();
        if (lastItem != null && lastItem != itemStack) {
            update = true;
            recipes = new ArrayList<>();
            for (EnchantmentRecipe recipe : client.world.getRecipeManager().listAllOfType(AArcanaRecipes.ENCHANTMENT_RECIPE_TYPE)) {
                if (recipe.enchantment.isAcceptableItem(itemStack)) {
                    recipes.add(recipe);
                }
            }
        }
        lastItem = itemStack;

        if (lastBackgroundHeight != context.getScaledWindowHeight() || lastBackgroundWidth != context.getScaledWindowWidth()) {
            update = true;
            lastBackgroundWidth = context.getScaledWindowWidth();
            lastBackgroundHeight = context.getScaledWindowHeight();
        }

        if (getScreenHandler().enchantmentPower[0] != lastPower) {
            update = true;
            lastPower = getScreenHandler().enchantmentPower[0];
        }

        int panelX = 161;
        int panelY = 9;
        int scaledPanelX = panelX * 2;
        int scaledPanelY = panelY * 2;
        int scaledPanelWidth = 118;

        int k = (int)(41.0F * this.scrollPosition);
        boolean hasRecipes = recipes != null && !recipes.isEmpty();
        context.drawTexture(OVERLAYS, 153, 9 + k, (hasRecipes && recipes.size() > 6 ? 0 : 6), 0, 6, 27);

        ItemStack stack = getScreenHandler().getSlot(0).getStack();

        if (!getScreenHandler().unlockedTreasures.isEmpty()) {
            AscendantArcana.LOGGER.debug("Unlocked treasure!");
        }

        if (stack != ItemStack.EMPTY) {

            int maxCapacity = AArcanaEnchantmentHelper.getEnchantmentCapacity(stack);
            int usedCapacity = AArcanaEnchantmentHelper.getEnchantmentUsage(stack);
            float multiplier = (float) usedCapacity / maxCapacity;
            if (multiplier > 1) multiplier = 1;

            context.drawTexture(OVERLAYS, 8, 110, 25, 0, MathHelper.floor(58 * multiplier), 5);

            if (hasRecipes && update) {
                clearEnchantments();
                int i = 0;
                for (EnchantmentRecipe recipe : recipes) {
                    addEnchantment(recipe, x + 68, y + 8 + (i * 19), i);
                    i++;
                }
            } else if (update) clearEnchantments();

            if (anySelected && !enchantments.get(selectedTile).locked && !enchantments.get(selectedTile).maxLevel) {
                context.drawTexture(OVERLAYS, panelX + 2, panelY + 105, 12, 0, 7, 7);
                context.drawTexture(OVERLAYS, panelX + 1, panelY + 113, 12, 8, 9, 7);
            } else if (anySelected && enchantments.get(selectedTile).locked) {
                context.drawTexture(OVERLAYS, panelX + 2, panelY + 47, 135, 0, 56, 57);
                context.getMatrices().push();
                context.getMatrices().peek().getPositionMatrix().scale(0.5F, 0.5F, 0.5F);
                MutableText text = Text.empty();
                if (AArcanaEnchantmentHelper.getRequiredEnchantmentPower(recipes.get(selectedTile).enchantment) > getScreenHandler().enchantmentPower[0]) {
                    text = Text.translatable("gui.enchanting.low_level");
                } else if (recipes.get(selectedTile).enchantment.isTreasure()) {
                    text = Text.translatable("gui.enchanting.treasure");
                }
                context.drawTextWrapped(textRenderer, text, scaledPanelX + 4, scaledPanelY + 100, scaledPanelWidth, 5592405);
                context.getMatrices().pop();
            } else {
                context.drawTexture(OVERLAYS, panelX + 2, panelY + 47, 191, 0, 56, 57);
            }
            context.getMatrices().push();
            context.getMatrices().peek().getPositionMatrix().scale(0.5F, 0.5F, 0.5F);
            if (anySelected) {
                EnchantmentRecipe recipe = recipes.get(selectedTile);
                MutableText enchantmentTitle = Text.translatable(recipe.enchantment.getTranslationKey()).formatted(Formatting.UNDERLINE);
                MutableText enchantmentDescription = Text.translatable(recipe.enchantment.getTranslationKey() + ".description");
                if (enchantments.get(selectedTile).locked) {
                    enchantmentTitle.formatted(Formatting.OBFUSCATED);
                    enchantmentDescription.formatted(Formatting.OBFUSCATED);
                }
                context.drawCenteredTextWithShadow(textRenderer, enchantmentTitle, scaledPanelX + 60, scaledPanelY + 2, 16777215);
                context.drawTextWrapped(textRenderer, enchantmentDescription, scaledPanelX + 2, scaledPanelY + 14, scaledPanelWidth, 5592405);
                if (!enchantments.get(selectedTile).locked && !enchantments.get(selectedTile).maxLevel) {
                    context.drawTextWrapped(textRenderer, Text.translatable("gui.enchanting.item_cost", recipe.magicalScrapCost, Text.translatable(AArcanaItems.ENCHANTED_SCRAP.getTranslationKey())), scaledPanelX + 42, scaledPanelY + 102, 76,16777215);
                    if (recipe.primaryIngredientStack != null) {
                        context.drawTextWrapped(textRenderer, Text.translatable("gui.enchanting.item_cost", recipe.primaryIngredientStack.getCount(), Text.translatable(recipe.primaryIngredientStack.getIngredient().getMatchingStacks()[0].getTranslationKey())), scaledPanelX + 42, scaledPanelY + 138, 76, 16777215);
                    }
                    if (recipe.secondaryIngredientStack != null) {
                        context.drawTextWrapped(textRenderer, Text.translatable("gui.enchanting.item_cost", recipe.secondaryIngredientStack.getCount(), Text.translatable(recipe.secondaryIngredientStack.getIngredient().getMatchingStacks()[0].getTranslationKey())), scaledPanelX + 42, scaledPanelY + 174, 76, 16777215);
                    }
                    textRenderer.drawWithOutline(Text.literal(String.valueOf(recipe.levelCost)).asOrderedText(), scaledPanelX + 12, scaledPanelY + 216, 5635925, 0, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), 15728880);
                    textRenderer.drawWithOutline(Text.literal(String.valueOf(AArcanaEnchantmentHelper.getEnchantmentCost(recipe.enchantment))).asOrderedText(), scaledPanelX + 14, scaledPanelY + 232, 16733525, 0, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), 15728880);

                }
            } else {
                context.drawCenteredTextWithShadow(textRenderer, Text.translatable(itemStack.getTranslationKey()).formatted(Formatting.UNDERLINE), scaledPanelX + 60, scaledPanelY + 2, 16777215);
                context.drawTextWrapped(textRenderer, Text.translatable("gui.enchanting.no_selection_body"), scaledPanelX + 2, scaledPanelY + 14, scaledPanelWidth, 5592405);
            }
            context.getMatrices().pop();
        } else {
            clearEnchantments();
            context.drawTexture(OVERLAYS, panelX + 2, panelY + 47, 191, 0, 56, 57);
            context.getMatrices().push();
            context.getMatrices().peek().getPositionMatrix().scale(0.5F, 0.5F, 0.5F);
            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("gui.enchanting.no_item_title").formatted(Formatting.UNDERLINE), scaledPanelX + 60, scaledPanelY + 2, 16777215);
            context.drawTextWrapped(textRenderer, Text.translatable("gui.enchanting.no_item_body"), scaledPanelX + 2, scaledPanelY + 14, scaledPanelWidth, 5592405);
            context.getMatrices().pop();
        }
    }

    public void addEnchantment(EnchantmentRecipe recipe, int buttonX, int buttonY, int i) {
        boolean locked = false;
        int power = getScreenHandler().enchantmentPower[0];
        int requiredPower = AArcanaEnchantmentHelper.getRequiredEnchantmentPower(recipe.enchantment);
        if (power < requiredPower) locked = true;
        if (recipe.enchantment.isTreasure() && !getScreenHandler().unlockedTreasures.contains(recipe.enchantment)) locked = true;
        EnchantmentTile tile = new EnchantmentTile(recipe, buttonX, buttonY, i, locked);
        enchantments.add(tile);
        addDrawableChild(tile);
    }

    public void clearEnchantments() {
        for (EnchantmentTile tile : enchantments) {
            remove(tile);
        }
        enchantments.clear();
        selectedTile = 0;
        anySelected = false;
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
        private final int i;
        protected boolean locked;
        protected boolean maxLevel = false;

        public EnchantmentTile(EnchantmentRecipe recipe, int x, int y, int i, boolean locked) {
            super(x, y, 0, 27, Text.translatable(recipe.enchantment.getTranslationKey()));
            this.locked = locked;
            this.i = i;
            this.recipe = recipe;
            this.width = 84;
            this.height = 19;
            if (lastItem.hasEnchantments()) {
                Map<Enchantment, Integer> enchants = EnchantmentHelper.get(lastItem);
                if (enchants.containsKey(recipe.enchantment) && enchants.get(recipe.enchantment) == recipe.enchantment.getMaxLevel()) this.maxLevel = true;
            }
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int v = 27;
            if (selectedTile == i && anySelected) v += 57;
            if (this.locked) v += 19;
            else if (this.maxLevel) v += 38;

            context.drawTexture(OVERLAYS, getX(), getY(), 0, v, width, height);

            context.getMatrices().push();
            context.getMatrices().peek().getPositionMatrix().scale(0.5F, 0.5F, 0.5F);
            Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();
            int scaledX = getX() * 2;
            int scaledY = getY() * 2;
            MutableText enchantText = Text.translatable(recipe.enchantment.getTranslationKey());
            if (enchantText.getString().length() > 15) enchantText = Text.literal(enchantText.asTruncatedString(14)).append("...");
            if (locked) enchantText.formatted(Formatting.OBFUSCATED);
            textRenderer.draw(enchantText, scaledX + 12, scaledY + 4, 5592405, false, positionMatrix, context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            MutableText levelText = null;
            if (maxLevel && !locked) levelText = Text.translatable("gui.enchanting.max_level");
            else if (!locked) {
                int level = 1;
                if (lastItem.hasEnchantments() && EnchantmentHelper.get(lastItem).containsKey(recipe.enchantment)) level = EnchantmentHelper.get(lastItem).get(recipe.enchantment) + 1;
                levelText = Text.translatable("gui.enchanting.level", Text.translatable("enchantment.level." + level), Text.translatable("enchantment.level." + recipe.enchantment.getMaxLevel()));
            }
            if (levelText != null) textRenderer.draw(levelText, scaledX + 12, scaledY + 14, 5592405, false, positionMatrix, context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);

            ItemStack magicalScraps = new ItemStack(AArcanaItems.ENCHANTED_SCRAP, recipe.magicalScrapCost);

            if (!maxLevel && !locked) {
                int itemX = scaledX + 108;
                int itemY = scaledY + 4;
                context.drawItem(magicalScraps, itemX, itemY);
                context.drawItemInSlot(textRenderer, magicalScraps, itemX, itemY);
                if (recipe.primaryIngredientStack != null) {
                    Item primaryIngredientItem = recipe.primaryIngredientStack.getIngredient().getMatchingStacks()[0].getItem();
                    ItemStack primaryIngredient = new ItemStack(primaryIngredientItem, recipe.primaryIngredientStack.getCount());
                    context.drawItem(primaryIngredient, itemX + 20, itemY);
                    context.drawItemInSlot(textRenderer, primaryIngredient, itemX + 20, itemY);
                }
                if (recipe.secondaryIngredientStack != null) {
                    Item secondaryIngredientItem = recipe.secondaryIngredientStack.getIngredient().getMatchingStacks()[0].getItem();
                    ItemStack secondaryIngredient = new ItemStack(secondaryIngredientItem, recipe.secondaryIngredientStack.getCount());
                    context.drawItem(secondaryIngredient, itemX + 40, itemY);
                    context.drawItemInSlot(textRenderer, secondaryIngredient, itemX + 40, itemY);
                }
                if (recipe.levelCost < 10) {
                    textRenderer.drawWithOutline(Text.literal(String.valueOf(recipe.levelCost)).asOrderedText(), scaledX + 160, scaledY + 28, 5635925, 0, positionMatrix, context.getVertexConsumers(), 15728880);
                } else {
                    textRenderer.drawWithOutline(Text.literal(String.valueOf(recipe.levelCost)).asOrderedText(), scaledX + 154, scaledY + 28, 5635925, 0, positionMatrix, context.getVertexConsumers(), 15728880);
                }
                textRenderer.drawWithOutline(Text.literal(String.valueOf(AArcanaEnchantmentHelper.getEnchantmentCost(recipe.enchantment))).asOrderedText(), scaledX + 144, scaledY + 28, 16733525, 0, positionMatrix, context.getVertexConsumers(), 15728880);
            }

            context.getMatrices().pop();
        }

        @Override
        public void onPress() {
            if (selectedTile == i && anySelected) {
                selectedTile = 0;
                anySelected = false;
            } else {
                selectedTile = i;
                anySelected = true;
            }
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }
}
