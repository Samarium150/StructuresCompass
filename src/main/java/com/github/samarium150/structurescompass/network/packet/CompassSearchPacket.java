package com.github.samarium150.structurescompass.network.packet;

import com.github.samarium150.structurescompass.item.StructuresCompassItem;
import com.github.samarium150.structurescompass.util.ItemUtils;
import com.github.samarium150.structurescompass.util.StructureUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Class for the compass to search a specific structure
 * @see StructuresCompassItem#search
 */
public final class CompassSearchPacket implements Packet {

    private final ResourceLocation resource;
    
    /**
     * Initializer of the packet
     * @param resource ResourceLocation
     */
    public CompassSearchPacket(ResourceLocation resource) {
        this.resource = resource;
    }
    
    /**
     * Decoder of the packet
     * @param buffer PacketBuffer
     */
    public CompassSearchPacket(@Nonnull PacketBuffer buffer) {
        resource = buffer.readResourceLocation();
    }

    public void toBytes(@Nonnull PacketBuffer buffer) {
        buffer.writeResourceLocation(resource);
    }

    public void handle(@Nonnull Supplier<Context> ctx) {
        Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            final ItemStack stack = ItemUtils.getHeldStructuresCompass(player);
            if (!stack.isEmpty() && player != null) {
                final ServerWorld world = (ServerWorld)player.level;
                Structure<?> structure = StructureUtils.getStructureForResource(resource);
                if (structure != null)
                    new Thread(() -> StructuresCompassItem.search(world, player, structure, stack)).start();
            }
        });
        context.setPacketHandled(true);
    }
}
