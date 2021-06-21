package com.github.samarium150.structurescompass.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class SendPacket {
    
    private final String message;
    
    public SendPacket(String str) {
        message = str;
    }
    
    public SendPacket(@Nonnull PacketBuffer buffer) {
        message = buffer.readString();
    }
    
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeString(this.message);
    }
    
    public void handle(@Nonnull Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            System.out.println(this.message);
            ServerPlayerEntity player = context.getSender();
            assert player != null;
            ServerWorld world = player.getServerWorld();
            StructureManager manager = world.getStructureManager();
            String msg = manager.getStructureStart(player.getPosition(), true, Structure.STRONGHOLD).getPos().toString();
            player.sendMessage(new TranslationTextComponent(msg), player.getUniqueID());
        });
        context.setPacketHandled(true);
    }
}
