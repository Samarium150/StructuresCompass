package io.github.samarium150.minecraft.mod.structures_compass.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Interface for packets sending through the network
 */
public interface Packet {
    
    /**
     * Encoder of the packet
     * @param buffer PacketBuffer
     * @see FriendlyByteBuf
     */
    void toBytes(@Nonnull FriendlyByteBuf buffer);
    
    /**
     * Consumer of the packet
     * @param ctx Supplier of Context
     * @see Context
     */
    void handle(@Nonnull Supplier<Context> ctx);
}
