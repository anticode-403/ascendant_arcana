package me.anticode.ascendant_arcana.recipe;

import com.google.gson.JsonObject;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import me.anticode.ascendant_arcana.init.AArcanaTags;
import me.anticode.ascendant_arcana.item.RelicItem;
import me.anticode.ascendant_arcana.logic.RelicHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

        public class InfusionRecipe implements SmithingRecipe {
            private final Identifier id;

            InfusionRecipe(Identifier id) {
                this.id = id;
            }

            @Override
            public boolean testTemplate(ItemStack stack) {
                return stack.isOf(AArcanaItems.INFUSION_SMITHING_TEMPLATE);
            }

            @Override
            public boolean testBase(ItemStack stack) {
                return stack.isDamageable();
            }

            @Override
            public boolean testAddition(ItemStack stack) {
                return stack.isIn(AArcanaTags.Items.RELICS);
            }

            @Override
            public boolean matches(Inventory inventory, World world) {
                return this.testTemplate(inventory.getStack(0)) && this.testBase(inventory.getStack(1)) && this.testAddition(inventory.getStack(2));
            }

            @Override
            public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
                ItemStack newStack = inventory.getStack(1).copy();
                ItemStack relicStack = inventory.getStack(2).copy();
                int relicStrength = RelicItem.getRelicStrength(relicStack);
                RelicHelper.Relics relicType = RelicItem.getRelicType(relicStack);
                Map<RelicHelper.Relics, Integer> relicsMap = RelicHelper.fromNbt((NbtList)newStack.getOrCreateNbt().get(RelicHelper.AARELICS_KEY));
                relicsMap.put(relicType, relicStrength);
                newStack.getOrCreateNbt().put(RelicHelper.AARELICS_KEY, RelicHelper.toNbt(relicsMap));
                return newStack;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AArcanaRecipes.INFUSION_RECIPE_SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<InfusionRecipe> {
        public InfusionRecipe read(Identifier id, JsonObject json) {
            return new InfusionRecipe(id);
        }

        public InfusionRecipe read(Identifier id, PacketByteBuf buf) {
            return new InfusionRecipe(id);
        }
        public void write(PacketByteBuf buf, InfusionRecipe recipe) {}
    }
}
