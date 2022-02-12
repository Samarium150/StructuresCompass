package io.github.samarium150.minecraft.mod.structures_compass.network.packet;

import io.github.samarium150.minecraft.mod.structures_compass.gui.StructuresCompassGUI;
import io.github.samarium150.minecraft.mod.structures_compass.util.GeneralUtils;
import io.github.samarium150.minecraft.mod.structures_compass.util.Serializer;
import io.github.samarium150.minecraft.mod.structures_compass.util.StructureUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

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
    private final List<StructureFeature<?>> allowed;
    private final HashMap<String, List<String>> map;
    
    /**
     * Initializer of the packet
     * @param stack ItemStack
     * @param allowed list of allowed
     * @param map hash map of structures to dimensions
     * @see StructureUtils#allowedStructures
     * @see StructureUtils#structuresDimensionMap
     */
    public SyncPacket(ItemStack stack, List<StructureFeature<?>> allowed, HashMap<String, List<String>> map) {
        this.stack = stack;
        this.allowed = allowed;
        this.map = map;
    }
    
    /**
     * Decoder of the packet
     * @param buffer PacketBuffer
     */
    public SyncPacket(@Nonnull FriendlyByteBuf buffer) {
        stack = buffer.readItem();
        allowed = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; ++i)
            allowed.add(StructureUtils.getStructureForResource(buffer.readResourceLocation()));
        HashMap<String, List<String>> temp = new HashMap<>();
        size = buffer.readInt();
        StringBuilder serialized = new StringBuilder();
        try {
            for (int i = 0; i < size; ++i) serialized.append(buffer.readUtf());
            temp = this.deserialize(serialized.toString());
            temp.replaceAll((k, v) -> v.stream().map(StructureUtils::getLocalizedDimensionName).collect(Collectors.toList()));
        } catch (Exception e) {
            GeneralUtils.logger.error(e);
        } finally {
            map = temp;
        }
    }
    
    @Override
    public void toBytes(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeItem(stack);
        buffer.writeInt(allowed.size());
        allowed.forEach(structure -> buffer.writeResourceLocation(StructureUtils.getResourceForStructure(structure)));
        String serialized;
        try {
            serialized = this.serialize(map);
        } catch (Exception e) {
            GeneralUtils.logger.error(e);
            return;
        }
        List<String> ret = GeneralUtils.splitEqually(serialized, 15000);
        int size = ret.size();
        buffer.writeInt(size);
        for (String s : ret)
            buffer.writeUtf(s);
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
