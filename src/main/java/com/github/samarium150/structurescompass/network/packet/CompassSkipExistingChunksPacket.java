package com.github.samarium150.structurescompass.network.packet;

import com.github.samarium150.structurescompass.item.StructuresCompassItem;
import com.github.samarium150.structurescompass.util.ItemUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Class for the compass to set the SkipExistingChunks property
 * @see net.minecraft.world.server.ServerWorld#getStructureLocation
 */
public final class CompassSkipExistingChunksPacket implements Packet {

    private final Boolean skip;
    
    /**
     * Initializer of the packet
     * @param skip the SkipExistingChunks property
     */
    public CompassSkipExistingChunksPacket(Boolean skip) {
        this.skip = skip;
    }
    
    /**
     * Decoder of the packet
     * @param buffer PacketBuffer
     */
    public CompassSkipExistingChunksPacket(@Nonnull PacketBuffer buffer) {
        skip = buffer.readBoolean();
    }

    public void toBytes(@Nonnull PacketBuffer buffer) {
        buffer.writeBoolean(skip);
    }

    public void handle(@Nonnull Supplier<Context> ctx) {
        Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            final ItemStack stack = ItemUtils.getHeldStructuresCompass(player);
            if (!stack.isEmpty() && player != null) StructuresCompassItem.setSkip(skip, stack);
        });
        context.setPacketHandled(true);
    }
}
