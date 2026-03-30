package me.anticode.ascendant_arcana.screenhandler;

import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import me.anticode.ascendant_arcana.init.AArcanaScreenHandlers;
import me.anticode.ascendant_arcana.recipe.EnchantmentRecipe;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AArcanaEnchantingScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final ScreenHandlerContext context;
    private int enchantmentPower = 0;
    private List<EnchantmentRecipe> recipes;

    public AArcanaEnchantingScreenHandler(int Id, PlayerInventory playerInventory) {
        this(Id, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public AArcanaEnchantingScreenHandler(int Id, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(AArcanaScreenHandlers.ENCHANTING, Id);

        this.context = context;

        inventory = new SimpleInventory(4);
        inventory.onOpen(playerInventory.player);

        this.addSlot(new EnchantableToolSlot(inventory, 0, 29, 27));
        this.addSlot(new MagicalScrapSlot(inventory, 1, 164, 60));
        this.addSlot(new EnchantmentIngredientSlot(inventory, 2, 164, 78));
        this.addSlot(new EnchantmentIngredientSlot(inventory, 3, 164, 96));

        int x, y;
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 36 + x * 18, 137 + y * 18));
            }
        }
        for (y = 0; y < 9; y++) {
            this.addSlot(new Slot(playerInventory, y, 36 + y * 18, 195));
        }
    }

    public List<EnchantmentRecipe> getRecipes() {
        return recipes;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (inventory != this.inventory) return;
        ItemStack itemStack = inventory.getStack(0);
        if (itemStack.isEmpty() || (!itemStack.hasEnchantments() && !itemStack.isEnchantable())) return;
        context.run((world, pos) -> {
            int i = 0;

            for (BlockPos blockPos : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
                if (EnchantingTableBlock.canAccessPowerProvider(world, pos, blockPos)) {
                    if (world.getBlockEntity(blockPos) instanceof ChiseledBookshelfBlockEntity chiseledBookshelf) {
                        for (int j = 0; j < chiseledBookshelf.size(); j++) {
                            ItemStack itemStack1 = chiseledBookshelf.getStack(j);
                            for (Map.Entry<Enchantment, Integer> enchantInstance : EnchantmentHelper.get(itemStack1).entrySet()) {
                                int rarityMultiplier = switch (enchantInstance.getKey().getRarity()) {
                                    case UNCOMMON -> 2;
                                    case RARE -> 3;
                                    case VERY_RARE -> 5;
                                    default -> 1;
                                };
                                i += enchantInstance.getValue() * rarityMultiplier;
                            }
                        }
                    } else i++;
                }
            }

            enchantmentPower = i;

            recipes = new ArrayList<>();
            for (EnchantmentRecipe recipe : world.getServer().getRecipeManager().listAllOfType(AArcanaRecipes.ENCHANTMENT_RECIPE_TYPE)) {
                if (recipe.enchantment.isAcceptableItem(itemStack) && EnchantmentHelper.isCompatible(EnchantmentHelper.get(itemStack).keySet(), recipe.enchantment)) {
                    recipes.add(recipe);
                }
            }
        });
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack moveStack = slot.getStack();
            stackCopy = moveStack.copy();
            if (index <= 3) {
                if (!this.insertItem(moveStack, 4, 40, true)) return ItemStack.EMPTY;
            } else if (moveStack.isEnchantable() || moveStack.hasEnchantments()) {
                if (!this.insertItem(moveStack, 0, 1, false)) return ItemStack.EMPTY;
            } else if (moveStack.isOf(AArcanaItems.ENCHANTED_SCRAP)) {
                if (!this.insertItem(moveStack, 1, 2, false)) return ItemStack.EMPTY;
            } else {
                if (!this.insertItem(moveStack, 2, 4, false)) return ItemStack.EMPTY;
            }

            if (moveStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();

            if (moveStack.getCount() == stackCopy.getCount()) return ItemStack.EMPTY;
            slot.onTakeItem(player, moveStack);
        }
        return stackCopy;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (inventory.isEmpty()) return;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack != ItemStack.EMPTY) {
                if (!this.insertItem(itemStack, 4, 40, true)) player.dropItem(itemStack, true);
            }
        }
    }

    private static class EnchantableToolSlot extends Slot {
        public EnchantableToolSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isEnchantable() || stack.hasEnchantments();
        }
    }

    private static class MagicalScrapSlot extends Slot {
        public MagicalScrapSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isOf(AArcanaItems.ENCHANTED_SCRAP);
        }
    }

    private static class EnchantmentIngredientSlot extends Slot {
        public EnchantmentIngredientSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return true;
        }
    }
}
