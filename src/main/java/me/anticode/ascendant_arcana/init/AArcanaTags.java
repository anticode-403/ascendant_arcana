package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class AArcanaTags {
    public static class Items {
        public static final TagKey<Item> RELICS = createItemTag("relics");
        public static final TagKey<Item> HEARTS = createItemTag("heart_items");

        private static TagKey<Item> createItemTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(AscendantArcana.modID, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> ENCHANTING_TABLES = createBlockTag("enchanting_tables");

        private static TagKey<Block> createBlockTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(AscendantArcana.modID, name));
        }
    }

    public static void initialize() {}
}
