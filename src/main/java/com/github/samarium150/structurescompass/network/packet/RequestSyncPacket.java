package com.github.samarium150.structurescompass.network.packet;

import com.github.samarium150.structurescompass.network.StructuresCompassNetwork;
import com.github.samarium150.structurescompass.util.ItemUtils;
import com.github.samarium150.structurescompass.util.StructureUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * Class for client to request data synchronization
 */
public final class RequestSyncPacket implements Packet {
    
    /**
     * Initializer of the packet
     */
    public RequestSyncPacket() { }
    
    /**
     * Decoder of the packet
     * @param buffer PacketBuffer
     */
    @SuppressWarnings("unused")
    public RequestSyncPacket(PacketBuffer buffer) { }
    
    public void toBytes(@Nonnull PacketBuffer buffer) { }
    
    public void handle(@Nonnull Supplier<Context> ctx) {
        Context context = ctx.get();
        context.enqueueWork(() -> {
            final ServerPlayerEntity player = context.getSender();
            final ItemStack stack = ItemUtils.getHeldStructuresCompass(player);
            if (!stack.isEmpty() && player != null) {
                final List<Structure<?>> allowed = StructureUtils.getAllowedStructures();
                final HashMap<String, List<String>> map = new HashMap<>();
                allowed.forEach(structure -> map.put(
                    StructureUtils.getStructureName(structure),
                    StructureUtils.getDimensions(player.getServerWorld(), structure)
                ));
                StructuresCompassNetwork.channel.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncPacket(stack, allowed, map)
                );
            }
        });
        context.setPacketHandled(true);
    }
}
