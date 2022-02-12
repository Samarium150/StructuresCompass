package io.github.samarium150.minecraft.mod.structures_compass.command;

import io.github.samarium150.minecraft.mod.structures_compass.item.StructuresCompassItem;
import io.github.samarium150.minecraft.mod.structures_compass.init.ItemRegistry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

/**
 * Command for getting a tagged compass
 * @deprecated This command is used for early development
 *     but could still be used in the game
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public final class GetCompass {
    
    private GetCompass() { }
    
    private static int getTaggedCompass(@Nonnull CommandSourceStack source, String feature) throws CommandSyntaxException {
        ItemStack structures_compass = new ItemStack(ItemRegistry.STRUCTURES_COMPASS.get());
        giveItem(source.getPlayerOrException(), StructuresCompassItem.setStructureName(feature, structures_compass));
        return Command.SINGLE_SUCCESS;
    }
    
    private static void giveItem(@Nonnull ServerPlayer player, ItemStack structures_compass) {
        ItemEntity itemEntity = player.drop(structures_compass, false);
        if (itemEntity != null) {
            itemEntity.setNoPickUpDelay();
            itemEntity.setOwner(player.getUUID());
        }
    }
    
    /**
     * Register this command
     * @param dispatcher CommandDispatcher
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> structures_compass = Commands.literal("structures_compass")
            .requires((commandSource) -> commandSource.hasPermission(2));

        for (StructureFeature<?> structureFeature : ForgeRegistries.STRUCTURE_FEATURES) {
            ResourceLocation res = structureFeature.getRegistryName();
            if (res == null) continue;
            String structure = res.toString();
            structures_compass.then(Commands.literal(structure)
                    .executes((context -> getTaggedCompass(context.getSource(), structure))));
        }
        dispatcher.register(structures_compass);
    }
}
