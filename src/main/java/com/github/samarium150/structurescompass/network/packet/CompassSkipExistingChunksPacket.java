package com.github.samarium150.structurescompass.network.packet;

import com.github.samarium150.structurescompass.item.StructuresCompassItem;
import com.github.samarium150.structurescompass.util.ItemUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Class for the compass to set the SkipExistingChunks property
 * @see net.minecraft.server.level.ServerLevel#findNearestMapFeature
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
    public CompassSkipExistingChunksPacket(@Nonnull FriendlyByteBuf buffer) {
        skip = buffer.readBoolean();
    }

    public void toBytes(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeBoolean(skip);
    }

    public void handle(@Nonnull Supplier<Context> ctx) {
        Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            final ItemStack stack = ItemUtils.getHeldStructuresCompass(player);
            if (!stack.isEmpty() && player != null) StructuresCompassItem.setSkip(skip, stack);
        });
        context.setPacketHandled(true);
    }
}
