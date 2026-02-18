package me.anticode.ascendant_arcana.recipe;

import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.init.AArcanaAttributes;
import me.anticode.ascendant_arcana.init.AArcanaItems;
import me.anticode.ascendant_arcana.init.AArcanaRecipes;
import me.anticode.ascendant_arcana.init.AArcanaTags;
import me.anticode.ascendant_arcana.item.RelicItem;
import me.anticode.ascendant_arcana.logic.RelicHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;

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
                if (relicType == RelicHelper.Relics.PROTECTION && newStack.getItem() instanceof ArmorItem armorItem) {
                    UUID uuid = switch (armorItem.getSlotType()) {
                        case EquipmentSlot.HEAD -> UUID.fromString("ccd7386d-62cf-4ef7-8cc1-a8a2ac7f942c");
                        case EquipmentSlot.CHEST -> UUID.fromString("610c3b9b-9c45-4845-8289-99dbe5034894");
                        case EquipmentSlot.LEGS -> UUID.fromString("e91f5ebf-3c02-43ec-a842-ce9b68a80c3a");
                        case EquipmentSlot.FEET -> UUID.fromString("93ef9100-4f32-45e0-8568-f837918e9b43");
                        default -> null;
                    };
                    NbtList attributes = (NbtList)newStack.getOrCreateNbt().get("AttributeModifiers");
                    if (attributes == null) attributes = new NbtList();
                    if (attributes.isEmpty()) {
                        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = newStack.getAttributeModifiers(armorItem.getSlotType());
                        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : modifiers.entries()) {
                            NbtCompound compound = entry.getValue().toNbt();
                            compound.putString("Slot", armorItem.getSlotType().getName());
                            compound.putString("AttributeName", Registries.ATTRIBUTE.getId(entry.getKey()).toString());
                            attributes.add(compound);
                        }
                    }
                    EntityAttributeModifier modifier = new EntityAttributeModifier(uuid, "Protection Relic Bonus", RelicHelper.convertStrengthIntoReal(RelicHelper.Relics.PROTECTION, relicStrength) * 0.01, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                    NbtCompound attributeCompound = modifier.toNbt();
                    attributeCompound.putString("Slot", armorItem.getSlotType().getName());
                    attributeCompound.putString("AttributeName", "ascendant_arcana:generic.protection");
                    attributes.add(attributeCompound);
                    newStack.getOrCreateNbt().put("AttributeModifiers", attributes);
                }
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
