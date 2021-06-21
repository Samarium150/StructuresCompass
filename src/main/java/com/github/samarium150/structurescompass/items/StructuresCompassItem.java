package com.github.samarium150.structurescompass.items;

import com.github.samarium150.structurescompass.network.SendPacket;
import com.github.samarium150.structurescompass.network.StructuresCompassNetwork;
import com.google.common.graph.Network;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StructuresCompassItem extends Item {
    
    public static final String NAME = "structures_compass";
    
    public StructuresCompassItem() {
        super(new Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1).rarity(Rarity.COMMON));
        setRegistryName(NAME);
    }
    
    @Override
    public int getItemEnchantability() {
        return 15;
    }
    
    @Override
    public int getUseDuration(@Nullable ItemStack itemstack) {
        return 0;
    }
    
    @Override
    public float getDestroySpeed(@Nullable ItemStack par1ItemStack, @Nullable BlockState par2Block) {
        return 1F;
    }
    
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(
        @Nonnull World world,
        @Nonnull PlayerEntity entity,
        @Nonnull Hand hand
    ) {
        if (world.isRemote) {
            StructuresCompassNetwork.channel.sendToServer(new SendPacket("network test from client"));
        } //else {
//            StructuresCompassNetwork.channel.send(
//                PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity),
//                new SendPacket("network test from server")
//            );
//        }
        
//        entity.sendMessage(new TranslationTextComponent(ForgeRegistries.STRUCTURE_FEATURES.getValues().toString()), entity.getUniqueID());
//        ItemStack itemstack = ar.getResult();
//        double x = entity.getPosX();
//        double y = entity.getPosY();
//        double z = entity.getPosZ();
//        if (entity instanceof ServerPlayerEntity) {
//            NetworkHooks.openGui((ServerPlayerEntity) entity, new INamedContainerProvider() {
//                @Override
//                @Nonnull
//                public ITextComponent getDisplayName() {
//                    return new StringTextComponent("Structures Compass");
//                }
//
//                @Override
//                public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
//                    PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
//                    packetBuffer.writeBlockPos(new BlockPos(x, y, z));
//                    packetBuffer.writeByte(hand == Hand.MAIN_HAND ? 0 : 1);
//                    return new StructuresCompassGUIGui.GuiContainerMod(id, inventory, packetBuffer);
//                }
//            }, buf -> {
//                buf.writeBlockPos(new BlockPos(x, y, z));
//                buf.writeByte(hand == Hand.MAIN_HAND ? 0 : 1);
//            });
//        }
        return super.onItemRightClick(world, entity, hand);
    }
}
