package me.anticode.ascendant_arcana.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import me.anticode.ascendant_arcana.logic.AArcanaEnchantmentHelper;
import me.anticode.ascendant_arcana.networking.EnchantingScreenRemoveRecipe;
import me.anticode.ascendant_arcana.networking.EnchantingScreenSendRecipe;
import me.anticode.ascendant_arcana.recipe.EnchantmentRecipe;
import me.anticode.ascendant_arcana.screenhandler.AArcanaEnchantingScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AArcanaEnchantingScreen extends HandledScreen<AArcanaEnchantingScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(AscendantArcana.modID, "textures/gui/container/enchanting_table.png");
    private static final Identifier OVERLAYS = new Identifier(AscendantArcana.modID, "textures/gui/container/enchanting_table_elements.png");
    List<EnchantmentRecipe> recipes = new ArrayList<>();
    private final List<EnchantmentTile> enchantments = new ArrayList<>();
    private LetsGoEnchantingButton enchantingButton;
    private boolean enchantingButtonEnabled = false;
    private float scrollPosition;
    private boolean scrollerClicked;
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
        if (lastItem != null && !lastItem.isOf(itemStack.getItem())) {
            update = true;
            recipes = new ArrayList<>();
            assert client != null;
            assert client.world != null;
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
        int scaledPanelWidth = 112;

        int k = (int)(94.0F * this.scrollPosition);
        boolean hasRecipes = recipes != null && !recipes.isEmpty();
        context.drawTexture(OVERLAYS, 153, 9 + k, (hasRecipes && recipes.size() > 6 ? 0 : 6), 0, 6, 27);

        ItemStack stack = getScreenHandler().getSlot(0).getStack();

        if (stack != ItemStack.EMPTY) {

            int maxCapacity = AArcanaEnchantmentHelper.getEnchantmentCapacity(stack);
            int usedCapacity = AArcanaEnchantmentHelper.getEnchantmentUsage(stack);
            float multiplier = (float) usedCapacity / maxCapacity;
            if (multiplier > 1) multiplier = 1;

            context.drawTexture(OVERLAYS, 8, 110, 25, 0, MathHelper.floor(58 * multiplier), 5);

            if (update) {
                clearEnchantments();
                scrollPosition = 0;
            }
            if (hasRecipes && update) {
                int i = 0;
                for (EnchantmentRecipe recipe : recipes) {
                    addEnchantment(recipe, x + 68, y + 8 + (i * 19), i);
                    i++;
                }
            }

            if (!enchantments.isEmpty()) {

                EnchantmentTile tile = enchantments.get(selectedTile);
                EnchantmentRecipe recipe = recipes.get(selectedTile);
                boolean withinCapacity = AArcanaEnchantmentHelper.testEnchantmentCost(stack, AArcanaEnchantmentHelper.getEnchantmentCost(recipe.enchantment));

                if (anySelected && !tile.locked && !tile.maxLevel && withinCapacity) {
                    if (AscendantArcana.config.disable_xp) {
                        context.drawTexture(OVERLAYS, panelX + 1, panelY + 105, 12, 8, 9, 7);
                    } else {
                        if (getScreenHandler().player.experienceLevel < recipe.levelCost) {
                            context.drawTexture(OVERLAYS, panelX + 2, panelY + 105, 19, 0, 6, 7);
                        } else {
                            context.drawTexture(OVERLAYS, panelX + 2, panelY + 105, 12, 0, 7, 7);
                        }
                        context.drawTexture(OVERLAYS, panelX + 1, panelY + 113, 12, 8, 9, 7);
                    }
                } else if (anySelected && (tile.locked || !withinCapacity)) {
                    context.drawTexture(OVERLAYS, panelX + 2, panelY + 47, 135, 0, 56, 57);
                    context.getMatrices().push();
                    context.getMatrices().peek().getPositionMatrix().scale(0.5F, 0.5F, 0.5F);
                    MutableText text = Text.empty();
                    if (recipe.enchantment.isTreasure() && tile.locked) {
                        text = Text.translatable("gui.enchanting.treasure");
                    } else if (AArcanaEnchantmentHelper.getRequiredEnchantmentPower(recipe.enchantment) > getScreenHandler().enchantmentPower[0] && tile.locked) {
                        text = Text.translatable("gui.enchanting.low_level");
                    } else if (!withinCapacity) {
                        text = Text.translatable("gui.enchanting.max_capacity");
                    }
                    context.drawTextWrapped(textRenderer, text, scaledPanelX + 4, scaledPanelY + 100, scaledPanelWidth, 5592405);
                    context.getMatrices().pop();
                } else {
                    context.drawTexture(OVERLAYS, panelX + 2, panelY + 47, 191, 0, 56, 57);
                }
                context.getMatrices().push();
                context.getMatrices().peek().getPositionMatrix().scale(0.5F, 0.5F, 0.5F);
                if (anySelected) {
                    MutableText enchantmentTitle = Text.translatable(recipe.enchantment.getTranslationKey()).formatted(Formatting.UNDERLINE);
                    MutableText enchantmentDescription = Text.translatable(recipe.enchantment.getTranslationKey() + ".description");
                    if (tile.locked) {
                        enchantmentTitle.formatted(Formatting.OBFUSCATED);
                        enchantmentDescription.formatted(Formatting.OBFUSCATED);
                    }
                    context.drawCenteredTextWithShadow(textRenderer, enchantmentTitle, scaledPanelX + 60, scaledPanelY + 2, 16777215);
                    context.drawTextWrapped(textRenderer, enchantmentDescription, scaledPanelX + 2, scaledPanelY + 14, scaledPanelWidth, 5592405);
                    if (!tile.locked && !tile.maxLevel && withinCapacity) {
                        int scrapColor = 16777215;
                        ItemStack scrapStack = getScreenHandler().getSlot(1).getStack();
                        if (scrapStack.getCount() < recipe.magicalScrapCost) {
                            scrapColor = 11141120;
                        }
                        context.drawTextWrapped(textRenderer, Text.translatable("gui.enchanting.item_cost", recipe.magicalScrapCost, Text.translatable(AArcanaItems.ENCHANTED_SCRAP.getTranslationKey())), scaledPanelX + 42, scaledPanelY + 102, 76,scrapColor);
                        ItemStack primaryItemStack = getScreenHandler().getSlot(2).getStack();
                        if (recipe.primaryIngredientStack != null) {
                            int color = 16777215;
                            if (!recipe.primaryIngredientStack.getIngredient().test(primaryItemStack) || primaryItemStack.getCount() < recipe.primaryIngredientStack.getCount()) {
                                color = 11141120;
                            }
                            context.drawTextWrapped(textRenderer, Text.translatable("gui.enchanting.item_cost", recipe.primaryIngredientStack.getCount(), Text.translatable(recipe.primaryIngredientStack.getIngredient().getMatchingStacks()[0].getTranslationKey())), scaledPanelX + 42, scaledPanelY + 138, 76, color);
                        }
                        ItemStack secondaryItemStack = getScreenHandler().getSlot(3).getStack();
                        if (recipe.secondaryIngredientStack != null) {
                            int color = 16777215;
                            if (!recipe.secondaryIngredientStack.getIngredient().test(secondaryItemStack) || secondaryItemStack.getCount() < recipe.secondaryIngredientStack.getCount()) {
                                color = 11141120;
                            }
                            context.drawTextWrapped(textRenderer, Text.translatable("gui.enchanting.item_cost", recipe.secondaryIngredientStack.getCount(), Text.translatable(recipe.secondaryIngredientStack.getIngredient().getMatchingStacks()[0].getTranslationKey())), scaledPanelX + 42, scaledPanelY + 174, 76, color);
                        }
                        if (AscendantArcana.config.disable_xp) {
                            textRenderer.drawWithOutline(Text.literal(String.valueOf(AArcanaEnchantmentHelper.getEnchantmentCost(recipe.enchantment))).asOrderedText(), scaledPanelX + 14, scaledPanelY + 216, 16733525, 0, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), 15728880);
                        } else {
                            textRenderer.drawWithOutline(Text.literal(String.valueOf(recipe.levelCost)).asOrderedText(), scaledPanelX + 12, scaledPanelY + 216, 5635925, 0, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), 15728880);
                            textRenderer.drawWithOutline(Text.literal(String.valueOf(AArcanaEnchantmentHelper.getEnchantmentCost(recipe.enchantment))).asOrderedText(), scaledPanelX + 14, scaledPanelY + 232, 16733525, 0, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), 15728880);
                        }
                        context.getMatrices().pop();

                        boolean buttonEnabled = AscendantArcana.config.disable_xp || recipe.levelCost <= getScreenHandler().player.experienceLevel;
                        if (recipe.magicalScrapCost > 0) {
                            if (!scrapStack.isOf(AArcanaItems.ENCHANTED_SCRAP) || recipe.magicalScrapCost > scrapStack.getCount()) buttonEnabled = false;
                        }
                        if (recipe.primaryIngredientStack != null) {
                            if (!recipe.primaryIngredientStack.getIngredient().test(primaryItemStack)) buttonEnabled = false;
                            else if (recipe.primaryIngredientStack.getCount() > primaryItemStack.getCount()) buttonEnabled = false;
                        }
                        if (recipe.secondaryIngredientStack != null) {
                            if (!recipe.secondaryIngredientStack.getIngredient().test(secondaryItemStack)) buttonEnabled = false;
                            else if (recipe.secondaryIngredientStack.getCount() > secondaryItemStack.getCount()) buttonEnabled = false;
                        }

                        if (update || enchantingButtonEnabled != buttonEnabled) {
                            remove(enchantingButton);
                            enchantingButton = null;
                        }
                        if (enchantingButton == null) {
                            enchantingButtonEnabled = buttonEnabled;
                            enchantingButton = new LetsGoEnchantingButton(x + 193, y + 116, buttonEnabled);
                            addDrawableChild(enchantingButton);
                        }
                    } else {
                        remove(enchantingButton);
                        enchantingButton = null;
                        context.getMatrices().pop();
                    }
                } else {
                    remove(enchantingButton);
                    enchantingButton = null;
                    context.drawCenteredTextWithShadow(textRenderer, Text.translatable(itemStack.getTranslationKey()).formatted(Formatting.UNDERLINE), scaledPanelX + 60, scaledPanelY + 2, 16777215);
                    context.drawTextWrapped(textRenderer, Text.translatable("gui.enchanting.no_selection_body"), scaledPanelX + 2, scaledPanelY + 14, scaledPanelWidth, 5592405);
                    context.getMatrices().pop();
                }
            }
        } else {
            remove(enchantingButton);
            enchantingButton = null;
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
            int i = this.x + 153;
            int j = this.y + 9;

            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 121)) {
                this.scrollerClicked = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrollerClicked && recipes != null && !recipes.isEmpty() && recipes.size() > 6) {
            int j = this.y + 9;
            int k = j + 121;
            this.scrollPosition = ((float)mouseY - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (recipes != null && !recipes.isEmpty() && recipes.size() > 6) {
            int recipeSize = recipes.size();
            float f = (float)amount / (float)-recipeSize;
            this.scrollPosition = MathHelper.clamp(this.scrollPosition - f, 0.0F, 1.0F);
        }

        return true;
    }

    private class EnchantmentTile extends PressableWidget {
        private final EnchantmentRecipe recipe;
        private final int i;
        protected boolean locked;
        protected boolean maxLevel = false;
        private int offset = 0;

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


            context.drawTexture(OVERLAYS, getX(), getY(), 0, v + offset, width, getHeight());

            if (getHeight() == 0) return;
            if (height == getHeight() && !locked && !maxLevel) {
                if (AscendantArcana.config.disable_xp) {
                    context.drawTexture(OVERLAYS, getX() + 74, getY() + 11, 12, 8, 9, 7);
                } else {
                    if (recipe.levelCost > getScreenHandler().player.experienceLevel) {
                        context.drawTexture(OVERLAYS, getX() + 76, getY() + 11, 19, 0, 6, 7);
                    } else {
                        context.drawTexture(OVERLAYS, getX() + 76, getY() + 11, 12, 0, 7, 7);
                    }
                    context.drawTexture(OVERLAYS, getX() + 66, getY() + 11, 12, 8, 9, 7);
                }
            }
            context.getMatrices().push();
            context.getMatrices().peek().getPositionMatrix().scale(0.5F, 0.5F, 0.5F);
            Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();
            int scaledX = getX() * 2;
            int scaledY = getY() * 2;
            MutableText enchantText = Text.translatable(recipe.enchantment.getTranslationKey());
            if (enchantText.getString().length() > 15) enchantText = Text.literal(enchantText.asTruncatedString(14)).append("...");
            if (locked) enchantText.formatted(Formatting.OBFUSCATED);
            if (getHeight() > 6) textRenderer.draw(enchantText, scaledX + 12, scaledY + 4, 5592405, false, positionMatrix, context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            MutableText levelText = null;
            if (maxLevel && !locked) levelText = Text.translatable("gui.enchanting.max_level");
            else if (!locked && height == getHeight()) {
                int level = 1;
                if (lastItem.hasEnchantments() && EnchantmentHelper.get(lastItem).containsKey(recipe.enchantment)) level = EnchantmentHelper.get(lastItem).get(recipe.enchantment) + 1;
                levelText = Text.translatable("gui.enchanting.level", Text.translatable("enchantment.level." + level), Text.translatable("enchantment.level." + recipe.enchantment.getMaxLevel()));
            }
            if (levelText != null) textRenderer.draw(levelText, scaledX + 12, scaledY + 14, 5592405, false, positionMatrix, context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);

            ItemStack magicalScraps = new ItemStack(AArcanaItems.ENCHANTED_SCRAP, recipe.magicalScrapCost);

            if (!maxLevel && !locked && (getHeight() > 10 && offset == 0)) {
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
                if (getHeight() == height) {
                    if (AscendantArcana.config.disable_xp) {
                        textRenderer.drawWithOutline(Text.literal(String.valueOf(AArcanaEnchantmentHelper.getEnchantmentCost(recipe.enchantment))).asOrderedText(), scaledX + 160, scaledY + 28, 16733525, 0, positionMatrix, context.getVertexConsumers(), 15728880);
                    } else {
                        if (recipe.levelCost < 10) {
                            textRenderer.drawWithOutline(Text.literal(String.valueOf(recipe.levelCost)).asOrderedText(), scaledX + 160, scaledY + 28, 5635925, 0, positionMatrix, context.getVertexConsumers(), 15728880);
                        } else {
                            textRenderer.drawWithOutline(Text.literal(String.valueOf(recipe.levelCost)).asOrderedText(), scaledX + 154, scaledY + 28, 5635925, 0, positionMatrix, context.getVertexConsumers(), 15728880);
                        }
                        textRenderer.drawWithOutline(Text.literal(String.valueOf(AArcanaEnchantmentHelper.getEnchantmentCost(recipe.enchantment))).asOrderedText(), scaledX + 144, scaledY + 28, 16733525, 0, positionMatrix, context.getVertexConsumers(), 15728880);
                    }
                }
            }

            context.getMatrices().pop();
        }

        @Override
        public int getHeight() {
            if (recipes.size() < 6) return super.getHeight();
            int height = super.getHeight();
            int absolutePositionTop = i * height;
            int absolutePositionBottom = i * height + height;
            int absoluteMax = recipes.size() * height;
            int absoluteScrollPositionTop = (int) ((absoluteMax * scrollPosition) - (122 * scrollPosition));
            int absoluteScrollPositionBottom = absoluteScrollPositionTop + 122;
            if (absoluteScrollPositionTop > absolutePositionTop) {
                int difference = absoluteScrollPositionTop - absolutePositionTop;
                height -= difference;
                offset = difference;
            } else if (absoluteScrollPositionBottom < absolutePositionBottom) {
                int difference = absolutePositionBottom - absoluteScrollPositionBottom;
                height -= difference;
                offset = 0;
            } else {
                offset = 0;
            }
            return Math.max(height, 0);
        }

        @Override
        public int getY() {
            int absoluteMax = recipes.size() * height;
            int absoluteScrollPositionTop = (int) ((absoluteMax * scrollPosition) - (122 * scrollPosition));
            return super.getY() + offset - absoluteScrollPositionTop;
        }


        @Override
        protected boolean clicked(double mouseX, double mouseY) {
            return this.active && this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.getHeight());
        }

        @Override
        public void onPress() {
            if (getHeight() == 0) return;
            if (selectedTile == i && anySelected) {
                selectedTile = 0;
                anySelected = false;
                ClientPlayNetworking.send(EnchantingScreenRemoveRecipe.Id, new EnchantingScreenRemoveRecipe(getScreenHandler().syncId).write());
            } else {
                selectedTile = i;
                anySelected = true;
                ClientPlayNetworking.send(EnchantingScreenSendRecipe.Id, new EnchantingScreenSendRecipe(getScreenHandler().syncId, recipe).write());
            }
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }

    private class LetsGoEnchantingButton extends PressableWidget {
        private final boolean enabled;

        public LetsGoEnchantingButton(int x, int y, boolean enabled) {
            super(x, y, 26, 12, Text.translatable("gui.enchanting.enchant"));
            this.enabled = enabled;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int u = 83;
            if (!enabled) u = 109;
            context.drawTexture(OVERLAYS, getX(), getY(), u, 0, getWidth(), getHeight());
            context.getMatrices().push();
            context.getMatrices().peek().getPositionMatrix().scale(0.5F);
            context.drawCenteredTextWithShadow(textRenderer, getTitle(), (getX() + 13) * 2, (getY() + 4) * 2, 16777215);
            context.getMatrices().pop();
        }

        @Override
        public void onPress() {
            if (!enabled) return;
            assert client != null;
            assert client.interactionManager != null;
            client.interactionManager.clickButton(getScreenHandler().syncId, 0);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }
}
