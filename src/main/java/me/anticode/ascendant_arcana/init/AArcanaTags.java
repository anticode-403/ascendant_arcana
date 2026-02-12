package me.anticode.ascendant_arcana.init;

import me.anticode.ascendant_arcana.AscendantArcana;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class AArcanaTags {
    public static class Items {
        public static final TagKey<Item> RELICS = createItemTag("relics");

        private static TagKey<Item> createItemTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(AscendantArcana.modID, name));
        }
    }

    public static void initialize() {}
}
