package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.item.EnchantedScrapItem;
import me.anticode.ascendant_arcana.item.RelicItem;
import me.anticode.ascendant_arcana.logic.Relics;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class AArcanaItems {
    private static final Identifier EMPTY_ARMOR_SLOT_HELMET_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
    private static final Identifier EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
    private static final Identifier EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
    private static final Identifier EMPTY_ARMOR_SLOT_BOOTS_TEXTURE = new Identifier("item/empty_armor_slot_boots");
    private static final Identifier EMPTY_SLOT_HOE_TEXTURE = new Identifier("item/empty_slot_hoe");
    private static final Identifier EMPTY_SLOT_AXE_TEXTURE = new Identifier("item/empty_slot_axe");
    private static final Identifier EMPTY_SLOT_SWORD_TEXTURE = new Identifier("item/empty_slot_sword");
    private static final Identifier EMPTY_SLOT_SHOVEL_TEXTURE = new Identifier("item/empty_slot_shovel");
    private static final Identifier EMPTY_SLOT_PICKAXE_TEXTURE = new Identifier("item/empty_slot_pickaxe");

    public static final String INFUSION_SMITHING_TEMPLATE_ID = "infusion";
    public static final Item INFUSION_SMITHING_TEMPLATE = register(new SmithingTemplateItem(
            Text.translatable("item.ascendant_arcana.smithing_template.infusion.applies_to").formatted(Formatting.BLUE),
            Text.translatable("item.ascendant_arcana.smithing_template.infusion.ingredients").formatted(Formatting.BLUE),
            Text.translatable("item.ascendant_arcana.smithing_template.infusion.title").formatted(Formatting.GRAY),
            Text.translatable("item.ascendant_arcana.smithing_template.infusion.base_slot_description"),
            Text.translatable("item.ascendant_arcana.smithing_template.infusion.additions_slot_description"),
            getGeneralToolSmithingBase(),
            List.of() // TODO: Create empty slot textures for relics
    ), "infusion_smithing_template");

    public static final Item RELIC = register(new RelicItem(new Item.Settings()), "relic");

    public static final Item ENCHANTED_SCRAP = register(new EnchantedScrapItem(new Item.Settings()), "enchanted_scrap");
    public static final Item RESTORINE = register(new Item(new Item.Settings()), "restorine");

    public static final Item WARDEN_HEART = register(new Item(new Item.Settings().rarity(Rarity.RARE).maxCount(1).fireproof()), "warden_heart");

    private static List<Identifier> getGeneralToolSmithingBase() {
        return List.of(EMPTY_SLOT_AXE_TEXTURE, EMPTY_SLOT_HOE_TEXTURE, EMPTY_SLOT_PICKAXE_TEXTURE,
                EMPTY_SLOT_SWORD_TEXTURE, EMPTY_SLOT_SHOVEL_TEXTURE, EMPTY_ARMOR_SLOT_BOOTS_TEXTURE,
                EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE, EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE, EMPTY_ARMOR_SLOT_HELMET_TEXTURE);
    }

    public static Item register(Item item, String id) {
        Identifier itemId = new Identifier(AscendantArcana.modID, id);
        return Registry.register(Registries.ITEM, itemId, item);
    }

    public static ItemStack after(Item item) {
        return new ItemStack(item);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
            itemGroup.addAfter(after(Items.LAPIS_LAZULI), AArcanaItems.ENCHANTED_SCRAP);
            itemGroup.addAfter(after(Items.AMETHYST_SHARD), AArcanaItems.RESTORINE);
            itemGroup.addAfter(after(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), AArcanaItems.INFUSION_SMITHING_TEMPLATE);

            for (int i = 0; i < Relics.values().length * 5; i++) {
                int relicId = MathHelper.floor((double) i / 5);
                int strength = i + 1 - (relicId * 5);
                Relics relicType = Relics.fromId(relicId);
                ItemStack stack = new ItemStack(RELIC);
                RelicItem.writeRelicData(stack, relicType, strength);
                itemGroup.add(stack);
            }
        });
    }
}
