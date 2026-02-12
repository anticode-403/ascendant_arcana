package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import me.anticode.ascendant_arcana.item.RelicItem;
import net.minecraft.item.Item;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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

    public static final Item INFUSION_SMITHING_TEMPLATE = register(new SmithingTemplateItem(
            Text.translatable("item.ascendant_arcana.smithing_template.infusion.applies_to").formatted(Formatting.BLUE),
            Text.translatable("item.ascendant_arcana.smithing_template.infusion.ingredients").formatted(Formatting.BLUE),
            Text.translatable("item.ascendant_arcana.smithing_template.infusion.title").formatted(Formatting.GRAY),
            Text.translatable("item.ascendant_arcana.smithing_template.infusion.base_slot_description"),
            Text.translatable("item.ascendant_arcana.smithing_template.infusion.additions_slot_description"),
            getGeneralToolSmithingBase(),
            List.of() // TODO: Create empty slot textures for relics
    ), "infusion_smithing_template");

    public static final Item DORMANT_RELIC = register(new RelicItem(new Item.Settings()), "dormant_relic");
    public static final Item STIRRING_RELIC = register(new RelicItem(new Item.Settings()), "stirring_relic");
    public static final Item WAKING_RELIC = register(new RelicItem(new Item.Settings()), "waking_relic");
    public static final Item AWAKENED_RELIC = register(new RelicItem(new Item.Settings()), "awakened_relic");
    public static final Item ASCENDANT_RELIC = register(new RelicItem(new Item.Settings()), "ascendant_relic");

    private static List<Identifier> getGeneralToolSmithingBase() {
        return List.of(EMPTY_SLOT_AXE_TEXTURE, EMPTY_SLOT_HOE_TEXTURE, EMPTY_SLOT_PICKAXE_TEXTURE,
                EMPTY_SLOT_SWORD_TEXTURE, EMPTY_SLOT_SHOVEL_TEXTURE, EMPTY_ARMOR_SLOT_BOOTS_TEXTURE,
                EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE, EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE, EMPTY_ARMOR_SLOT_HELMET_TEXTURE);
    }

    public static Item register(Item item, String id) {
        Identifier itemId = new Identifier(AscendantArcana.modID, id);
        return Registry.register(Registries.ITEM, itemId, item);
    }

    public static void initialize() {}
}
