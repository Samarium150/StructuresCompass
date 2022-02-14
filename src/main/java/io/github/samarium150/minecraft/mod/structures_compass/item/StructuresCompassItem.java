package io.github.samarium150.minecraft.mod.structures_compass.item;

import io.github.samarium150.minecraft.mod.structures_compass.config.StructuresCompassConfig;
import io.github.samarium150.minecraft.mod.structures_compass.network.StructuresCompassNetwork;
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.CompassSearchPacket;
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.RequestSyncPacket;
import io.github.samarium150.minecraft.mod.structures_compass.util.GeneralUtils;
import io.github.samarium150.minecraft.mod.structures_compass.util.ItemUtils;
import io.github.samarium150.minecraft.mod.structures_compass.util.StructureUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1).rarity(Rarity.COMMON));
    }
    
    /**
     * Set the tag of structure's ResourceLocation
     * @param name the ResourceLocation of the structure
     * @param stack ItemStack
     * @return the given ItemStack
     */
    @Nonnull
    public static ItemStack setStructureName(String name, @Nonnull ItemStack stack) {
        CompoundTag tag = ItemUtils.getOrCreateItemTag(stack);
        if (tag != null) {
            tag.put(STRUCTURE_TAG, StringTag.valueOf(name));
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
        CompoundTag tag = ItemUtils.getItemTag(stack);
        return (tag != null) ? tag.getString(STRUCTURE_TAG) : null;
    }
    
    /**
     * Set the tag of structure's block pos
     * @param pos the block pos
     * @param stack ItemStack
     */
    public static void setPos(BlockPos pos, @Nonnull ItemStack stack) {
        CompoundTag tag = ItemUtils.getOrCreateItemTag(stack);
        if (tag != null) {
            tag.put(POS_TAG, LongTag.valueOf(pos.asLong()));
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
        CompoundTag tag = ItemUtils.getItemTag(stack);
        return (tag != null && tag.contains(POS_TAG, Tag.TAG_LONG)) ?
                   BlockPos.of(tag.getLong(POS_TAG)) : null;
    }
    
    /**
     * Set the tag of the ResourceLocation of structure's dimension
     * @param dimension the ResourceLocation of the dimension
     * @param stack ItemStack
     */
    public static void setDimension(String dimension, @Nonnull ItemStack stack) {
        CompoundTag tag = ItemUtils.getOrCreateItemTag(stack);
        if (tag != null) {
            tag.put(DIM_TAG, StringTag.valueOf(dimension));
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
        CompoundTag tag = ItemUtils.getItemTag(stack);
        return (tag != null) ? tag.getString(DIM_TAG) : null;
    }
    
    /**
     * Set the tag of SkipExistingChunks property
     * @param skip SkipExistingChunks
     * @param stack ItemStack
     */
    public static void setSkip(Boolean skip, @Nonnull ItemStack stack) {
        CompoundTag tag = ItemUtils.getOrCreateItemTag(stack);
        if (tag != null) {
            if (!skip) {
                ItemUtils.removeTag(stack, SKIP_TAG);
                return;
            }
            tag.put(SKIP_TAG, ByteTag.valueOf(true));
            stack.setTag(tag);
        }
    }
    
    /**
     * Get the tag of SkipExistingChunks property
     * @param stack ItemStack
     * @return SkipExistingChunks property
     */
    public static boolean isSkip(@Nonnull ItemStack stack) {
        CompoundTag tag = ItemUtils.getItemTag(stack);
        return tag != null && tag.getBoolean(SKIP_TAG);
    }
    
    /**
     * Search the given structure in the server world
     * @param world ServerWorld
     * @param player PlayerEntity
     * @param structure Structure
     * @param stack ItemStack
     * @see ServerLevel#findNearestMapFeature
     */
    public static void search(@Nonnull ServerLevel world, Player player, @Nonnull StructureFeature<?> structure, ItemStack stack) {
        ResourceLocation registry = structure.getRegistryName();
        assert registry != null;
        setStructureName(registry.toString(), stack);
        sendTranslatedMessage(GeneralUtils.prefix + "msg_searching", player);
        BlockPos pos = world.findNearestMapFeature(
            structure, player.blockPosition(), StructuresCompassConfig.radius.get(), isSkip(stack)
        );
        sendTranslatedMessage(GeneralUtils.prefix + "msg_done", player);
        if (pos == null) {
            ItemUtils.removeTag(stack, DIM_TAG);
            ItemUtils.removeTag(stack, POS_TAG);
        } else {
            Vec3 dis = StructureUtils.getDistance(pos, player);
            double distance = (double) Math.round(dis.length() * 100) / 100;
            if (distance > StructuresCompassConfig.maxDistance.get()) {
                ItemUtils.removeTag(stack, DIM_TAG);
                ItemUtils.removeTag(stack, POS_TAG);
            } else {
                setDimension(world.dimension().location().toString(), stack);
                setPos(pos, stack);
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    private static void sendMessage(String msg, @Nonnull Player entity) {
        entity.sendMessage(new TextComponent(msg), entity.getUUID());
    }
    
    private static void sendTranslatedMessage(String translationKey, @Nonnull Player entity) {
        entity.sendMessage(new TranslatableComponent(translationKey), entity.getUUID());
    }
    
    /**
     * Handle the right-clicking event
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
    public InteractionResultHolder<ItemStack> use(
        @Nonnull Level world,
        @Nonnull Player player,
        @Nonnull InteractionHand hand
    ) {
        ItemStack stack = player.getMainHandItem();
        if (world.isClientSide)
            if (player.isCrouching())
                StructuresCompassNetwork.channel.sendToServer(new RequestSyncPacket());
            else {
                String name = getStructureName(stack);
                if (name == null) {
                    sendMessage(I18n.get(GeneralUtils.prefix + "msg_no_target"), player);
                    return super.use(world, player, hand);
                }
                StructureFeature<?> structure = StructureFeature.STRUCTURES_REGISTRY.get(name.replace("minecraft:", ""));
                if (structure == null) {
                    sendMessage(I18n.get(GeneralUtils.prefix + "msg_error_name") + name, player);
                    return super.use(world, player, hand);
                }
                StructuresCompassNetwork.channel.sendToServer(
                    new CompassSearchPacket(StructureUtils.getResourceForStructure(structure))
                );
            }
        return super.use(world, player, hand);
    }
}
