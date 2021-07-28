package com.github.samarium150.structurescompass.command;

import com.github.samarium150.structurescompass.init.ItemRegistry;
import com.github.samarium150.structurescompass.item.StructuresCompassItem;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;
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
    
    private static int getTaggedCompass(@Nonnull CommandSource source, String feature) throws CommandSyntaxException {
        ItemStack structures_compass = new ItemStack(ItemRegistry.STRUCTURES_COMPASS.get());
        giveItem(source.asPlayer(), StructuresCompassItem.setStructureName(feature, structures_compass));
        return Command.SINGLE_SUCCESS;
    }
    
    private static void giveItem(@Nonnull ServerPlayerEntity player, ItemStack structures_compass) {
        ItemEntity itemEntity = player.dropItem(structures_compass, false);
        if (itemEntity != null) {
            itemEntity.setNoPickupDelay();
            itemEntity.setOwnerId(player.getUniqueID());
        }
    }
    
    /**
     * Register this command
     * @param dispatcher CommandDispatcher
     */
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> structures_compass = Commands.literal("structures_compass")
            .requires((commandSource) -> commandSource.hasPermissionLevel(2));

        for (Structure<?> structureFeature : ForgeRegistries.STRUCTURE_FEATURES) {
            ResourceLocation res = structureFeature.getRegistryName();
            if (res == null) continue;
            String structure = res.toString();
            structures_compass.then(Commands.literal(structure)
                    .executes((context -> getTaggedCompass(context.getSource(), structure))));
        }
        dispatcher.register(structures_compass);
    }
}
