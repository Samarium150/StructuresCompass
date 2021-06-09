package com.github.samarium150.structurescompass.item;

import com.github.samarium150.structurescompass.config.StructuresCompassConfig;
import com.github.samarium150.structurescompass.network.StructuresCompassNetwork;
import com.github.samarium150.structurescompass.network.packet.CompassSearchPacket;
import com.github.samarium150.structurescompass.network.packet.RequestSyncPacket;
import com.github.samarium150.structurescompass.util.ItemUtils;
import com.github.samarium150.structurescompass.util.StructureUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The compass item in the mod
 */
public final class StructuresCompassItem extends Item {
    
    public static final String NAME = "structures_compass";
    private static final String STRUCTURE_TAG = "Structure";
    private static final String DIM_TAG = "Dimension";
    private static final String POS_TAG = "Position";
    private static final String SKIP_TAG = "SkipExistingChunks";
    
    /**
     * Initializer of the item
     */
    public StructuresCompassItem() {
        super(new Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1).rarity(Rarity.COMMON));
        this.addPropertyOverride(new ResourceLocation("angle"), new StructuresCompassItemPropertyGetter());
    }
    
    /**
     * Set the tag of structure's ResourceLocation
     * @param name the ResourceLocation of the structure
     * @param stack ItemStack
     * @return the given ItemStack
     */
    @Nonnull
    public static ItemStack setStructureName(String name, @Nonnull ItemStack stack) {
        CompoundNBT tag = ItemUtils.getOrCreateItemTag(stack);
        if (tag != null) {
            tag.put(STRUCTURE_TAG, StringNBT.valueOf(name));
            stack.setTag(tag);
        }
        return stack;
    }
    
    /**
     * Get the tag of structure's ResourceLocation
     * @param stack ItemStack
     * @return the structure's ResourceLocation
     */
    @Nullable
    public static String getStructureName(@Nonnull ItemStack stack) {
        CompoundNBT tag = ItemUtils.getItemTag(stack);
        return (tag != null) ? tag.getString(STRUCTURE_TAG) : null;
    }
    
    /**
     * Set the tag of structure's block pos
     * @param pos the block pos
     * @param stack ItemStack
     */
    public static void setPos(BlockPos pos, @Nonnull ItemStack stack) {
        CompoundNBT tag = ItemUtils.getOrCreateItemTag(stack);
        if (tag != null) {
            tag.put(POS_TAG, LongNBT.valueOf(pos.toLong()));
            stack.setTag(tag);
        }
    }
    
    /**
     * Get the tag of structure's block pos
     * @param stack ItemStack
     * @return the block pos
     */
    @Nullable
    public static BlockPos getPos(@Nonnull ItemStack stack) {
        CompoundNBT tag = ItemUtils.getItemTag(stack);
        return (tag != null && tag.contains(POS_TAG, Constants.NBT.TAG_LONG)) ?
                   BlockPos.fromLong(tag.getLong(POS_TAG)) : null;
    }
    
    /**
     * Set the tag of the ResourceLocation of structure's dimension
     * @param dimension the ResourceLocation of the dimension
     * @param stack ItemStack
     */
    public static void setDimension(String dimension, @Nonnull ItemStack stack) {
        CompoundNBT tag = ItemUtils.getOrCreateItemTag(stack);
        if (tag != null) {
            tag.put(DIM_TAG, StringNBT.valueOf(dimension));
            stack.setTag(tag);
        }
    }
    
    /**
     * Get the tag of the ResourceLocation of structure's dimension
     * @param stack ItemStack
     * @return the given ItemStack
     */
    @Nullable
    public static String getDimension(@Nonnull ItemStack stack) {
        CompoundNBT tag = ItemUtils.getItemTag(stack);
        return (tag != null) ? tag.getString(DIM_TAG) : null;
    }
    
    /**
     * Set the tag of SkipExistingChunks property
     * @param skip SkipExistingChunks
     * @param stack ItemStack
     */
    public static void setSkip(Boolean skip, @Nonnull ItemStack stack) {
        CompoundNBT tag = ItemUtils.getOrCreateItemTag(stack);
        if (tag != null) {
            if (!skip) {
                ItemUtils.removeTag(stack, SKIP_TAG);
                return;
            }
            tag.put(SKIP_TAG, ByteNBT.valueOf(true));
            stack.setTag(tag);
        }
    }
    
    /**
     * Get the tag of SkipExistingChunks property
     * @param stack ItemStack
     * @return SkipExistingChunks property
     */
    public static boolean isSkip(@Nonnull ItemStack stack) {
        CompoundNBT tag = ItemUtils.getItemTag(stack);
        return tag != null && tag.getBoolean(SKIP_TAG);
    }
    
    /**
     * Search the given structure in the server world
     * @param world ServerWorld
     * @param player PlayerEntity
     * @param structure Structure
     * @param stack ItemStack
     * @see ServerWorld#findNearestStructure
     */
    public static void search(
        @Nonnull ServerWorld world, PlayerEntity player,
        @Nonnull Structure<?> structure, ItemStack stack
    ) {
        ResourceLocation registry = structure.getRegistryName();
        assert registry != null;
        setStructureName(registry.toString(), stack);
        sendTranslatedMessage("string.structurescompass.msg_searching", player);
        BlockPos pos = world.findNearestStructure(
            registry.toString().replace("minecraft:", ""),
            player.getPosition(), StructuresCompassConfig.radius.get(), isSkip(stack)
        );
        sendTranslatedMessage("string.structurescompass.msg_done", player);
        if (pos == null) {
            ItemUtils.removeTag(stack, DIM_TAG);
            ItemUtils.removeTag(stack, POS_TAG);
        } else {
            Vec3d dis = StructureUtils.getDistance(pos, player);
            double distance = (double) Math.round(dis.length() * 100) / 100;
            if (distance > StructuresCompassConfig.maxDistance.get()) {
                ItemUtils.removeTag(stack, DIM_TAG);
                ItemUtils.removeTag(stack, POS_TAG);
            } else {
                setDimension(world.getDimension().getType().toString(), stack);
                setPos(pos, stack);
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    private static void sendMessage(String msg, @Nonnull PlayerEntity entity) {
        entity.sendMessage(new StringTextComponent(msg));
    }
    
    private static void sendTranslatedMessage(String translationKey, @Nonnull PlayerEntity entity) {
        entity.sendMessage(new TranslationTextComponent(translationKey));
    }
    
    /**
     * Handle the right clicking event
     * <p>
     * On the server side, do nothing
     * <p>
     * On the client side, if the player is Crouching, send packet to server to open GUI;
     * otherwise, do searching according to the NBT of the compass
     * @param world World
     * @param player PlayerEntity
     * @param hand Hand
     * @return ActionResult
     */
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(
        @Nonnull World world,
        @Nonnull PlayerEntity player,
        @Nonnull Hand hand
    ) {
        ItemStack stack = player.getHeldItemMainhand();
        if (world.isRemote)
            if (player.isCrouching())
                StructuresCompassNetwork.channel.sendToServer(new RequestSyncPacket());
            else {
                String name = getStructureName(stack);
                if (name == null) {
                    sendMessage(I18n.format("string.structurescompass.msg_no_target"), player);
                    return super.onItemRightClick(world, player, hand);
                }
                Structure<?> structure = Feature.STRUCTURES.get(name.replace("minecraft:", ""));
                if (structure == null) {
                    sendMessage(I18n.format("string.structurescompass.msg_error_name") + name, player);
                    return super.onItemRightClick(world, player, hand);
                }
                StructuresCompassNetwork.channel.sendToServer(
                    new CompassSearchPacket(StructureUtils.getResourceForStructure(structure))
                );
            }
        return super.onItemRightClick(world, player, hand);
    }
}
