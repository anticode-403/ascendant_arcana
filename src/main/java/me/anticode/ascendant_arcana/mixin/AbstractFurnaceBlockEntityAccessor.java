package me.anticode.ascendant_arcana.mixin;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
    @Invoker("dropExperience")
    static void ascendant_arcana$dropExperience(ServerWorld world, Vec3d pos, int multiplier, float experience) {
        throw new UnsupportedOperationException();
    }
}
