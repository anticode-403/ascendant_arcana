package me.anticode.ascendant_arcana.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.anticode.ascendant_arcana.screenhandler.AArcanaEnchantingScreenHandler;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
    @ModifyReturnValue(method = "createScreenHandlerFactory", at = @At("RETURN"))
    private NamedScreenHandlerFactory createScreenHandlerFactory(NamedScreenHandlerFactory original, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos) {
        if (original == null) return null;
        else return new SimpleNamedScreenHandlerFactory(((syncId, playerInventory, player) -> new AArcanaEnchantingScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos))), Text.translatable("container.enchant"));
    }
}
