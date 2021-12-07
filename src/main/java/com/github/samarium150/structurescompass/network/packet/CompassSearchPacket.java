package com.github.samarium150.structurescompass.network.packet;

import com.github.samarium150.structurescompass.item.StructuresCompassItem;
import com.github.samarium150.structurescompass.util.ItemUtils;
import com.github.samarium150.structurescompass.util.StructureUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent.Context;

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
    public CompassSearchPacket(@Nonnull FriendlyByteBuf buffer) {
        resource = buffer.readResourceLocation();
    }

    public void toBytes(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(resource);
    }

    public void handle(@Nonnull Supplier<Context> ctx) {
        Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            final ItemStack stack = ItemUtils.getHeldStructuresCompass(player);
            if (!stack.isEmpty() && player != null) {
                final ServerLevel world = (ServerLevel)player.level;
                StructureFeature<?> structure = StructureUtils.getStructureForResource(resource);
                if (structure != null)
                    new Thread(() -> StructuresCompassItem.search(world, player, structure, stack)).start();
            }
        });
        context.setPacketHandled(true);
    }
}
