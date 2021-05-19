package com.github.samarium150.structurescompass.network.packet;

import com.github.samarium150.structurescompass.gui.StructuresCompassGUI;
import com.github.samarium150.structurescompass.util.GeneralUtils;
import com.github.samarium150.structurescompass.util.Serializer;
import com.github.samarium150.structurescompass.util.StructureUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Class for server to synchronize data
 */
public final class SyncPacket implements Packet, Serializer<HashMap<String, List<String>>> {
    
    private final ItemStack stack;
    private final List<Structure<?>> allowed;
    private final HashMap<String, List<String>> map;
    
    /**
     * Initializer of the packet
     * @param stack ItemStack
     * @param allowed list of allowed
     * @param map hash map of structures to dimensions
     * @see StructureUtils#allowedStructures
     * @see StructureUtils#structuresDimensionMap
     */
    public SyncPacket(ItemStack stack, List<Structure<?>> allowed, HashMap<String, List<String>> map) {
        this.stack = stack;
        this.allowed = allowed;
        this.map = map;
    }
    
    /**
     * Decoder of the packet
     * @param buffer PacketBuffer
     */
    public SyncPacket(@Nonnull PacketBuffer buffer) {
        stack = buffer.readItemStack();
        allowed = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; ++i)
            allowed.add(StructureUtils.getStructureForResource(buffer.readResourceLocation()));
        HashMap<String, List<String>> temp = new HashMap<>();
        try {
            temp = this.deserialize(buffer.readString());
            temp.replaceAll((k, v) -> v.stream().map(StructureUtils::getLocalizedDimensionName).collect(Collectors.toList()));
        } catch (Exception e) {
            GeneralUtils.logger.error(e);
        } finally {
            map = temp;
        }
    }
    
    @Override
    public void toBytes(@Nonnull PacketBuffer buffer) {
        buffer.writeItemStack(stack);
        buffer.writeInt(allowed.size());
        allowed.forEach(structure -> buffer.writeResourceLocation(StructureUtils.getResourceForStructure(structure)));
        String serialized;
        try {
            serialized = this.serialize(map);
        } catch (Exception e) {
            GeneralUtils.logger.error(e);
            return;
        }
        buffer.writeString(serialized);
    }
    
    @Override
    public void handle(@Nonnull Supplier<Context> ctx) {
        Context context = ctx.get();
        context.enqueueWork(() -> {
            StructureUtils.allowedStructures = allowed;
            StructureUtils.structuresDimensionMap = map;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> StructuresCompassGUI.openGUI(stack));
        });
        context.setPacketHandled(true);
    }
}
