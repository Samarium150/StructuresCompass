package com.github.samarium150.structurescompass.network.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Interface for packets sending through the network
 */
public interface Packet {
    
    /**
     * Encoder of the packet
     * @param buffer PacketBuffer
     * @see PacketBuffer
     */
    void toBytes(@Nonnull PacketBuffer buffer);
    
    /**
     * Consumer of the packet
     * @param ctx Supplier of Context
     * @see Context
     */
    void handle(@Nonnull Supplier<Context> ctx);
}
