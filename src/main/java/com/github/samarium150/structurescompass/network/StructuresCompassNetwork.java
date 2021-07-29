package com.github.samarium150.structurescompass.network;

import com.github.samarium150.structurescompass.network.packet.CompassSearchPacket;
import com.github.samarium150.structurescompass.network.packet.CompassSkipExistingChunksPacket;
import com.github.samarium150.structurescompass.network.packet.RequestSyncPacket;
import com.github.samarium150.structurescompass.network.packet.SyncPacket;
import com.github.samarium150.structurescompass.util.GeneralUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

/**
 * Class for handling network events
 */
public final class StructuresCompassNetwork {
    
    private static final String VERSION = "1.0.0";
    
    /**
     * Network channel of the mod
     */
    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(GeneralUtils.MOD_ID, "network"),
        () -> VERSION,
        version -> version.equals(VERSION),
        version -> version.equals(VERSION)
    );
    
    private static int ID = 0;
    
    private StructuresCompassNetwork() { }
    
    private static int next() {
        return ID++;
    }
    
    /**
     * Initialize the network
     */
    public static void init() {
        channel.messageBuilder(RequestSyncPacket.class, next())
            .encoder(RequestSyncPacket::toBytes)
            .decoder(RequestSyncPacket::new)
            .consumer(RequestSyncPacket::handle)
            .add();
        channel.messageBuilder(SyncPacket.class, next())
            .encoder(SyncPacket::toBytes)
            .decoder(SyncPacket::new)
            .consumer(SyncPacket::handle)
            .add();
        channel.messageBuilder(CompassSearchPacket.class, next())
            .encoder(CompassSearchPacket::toBytes)
            .decoder(CompassSearchPacket::new)
            .consumer(CompassSearchPacket::handle)
            .add();
        channel.messageBuilder(CompassSkipExistingChunksPacket.class, next())
            .encoder(CompassSkipExistingChunksPacket::toBytes)
            .decoder(CompassSkipExistingChunksPacket::new)
            .consumer(CompassSkipExistingChunksPacket::handle)
            .add();
    }
}
