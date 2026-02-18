package me.anticode.ascendant_arcana.init;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AArcanaAttributes {
    public static final EntityAttribute PROTECTION = register("ascendant_arcana:generic.protection", new ClampedEntityAttribute("attribute.ascendant_arcana.generic.protection", 1.0d, 0.0d, 100.0d));
    public static final EntityAttribute SWIFTNESS = register("ascendant_arcana:generic.swiftness", new ClampedEntityAttribute("attribute.ascendant_arcana.player.haste", 1.0d, 0.0d, 100.0d));

    private static EntityAttribute register(String id, final EntityAttribute attribute) {
        return Registry.register(Registries.ATTRIBUTE, id, attribute);
    }

    public static void initialize() {}
}
