package io.github.samarium150.minecraft.mod.structures_compass.network;

import io.github.samarium150.minecraft.mod.structures_compass.network.packet.CompassSearchPacket;
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.CompassSkipExistingChunksPacket;
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.RequestSyncPacket;
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.SyncPacket;
import io.github.samarium150.minecraft.mod.structures_compass.util.GeneralUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

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
