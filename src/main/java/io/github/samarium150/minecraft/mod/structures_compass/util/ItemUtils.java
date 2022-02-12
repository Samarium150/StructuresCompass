package io.github.samarium150.minecraft.mod.structures_compass.util;

import io.github.samarium150.minecraft.mod.structures_compass.init.ItemRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utilities related to items
 */
public abstract class ItemUtils {
    
    private ItemUtils() { }
    
    /**
     * Check whether the ItemStack is the compass
     * @param stack the given ItemStack
     * @return <code>true</code> if the ItemStack is the compass;
     *         <code>false</code> otherwise
     */
    public static boolean isStackItemStructuresCompass(@Nonnull ItemStack stack) {
        return stack.getItem() == ItemRegistry.STRUCTURES_COMPASS.get();
    }
    
    /**
     * Get the NBT on the ItemStack, create a new one if not present
     * @param stack the given ItemStack
     * @return <code>CompoundNBT</code> if the stack is the compass;
     *         <code>null</code> otherwise
     */
    @Nullable
    public static CompoundTag getOrCreateItemTag(@Nonnull ItemStack stack) {
        if (isStackItemStructuresCompass(stack)) {
            CompoundTag tag = stack.getTag();
            return (tag == null) ? new CompoundTag() : tag;
        }
        return null;
    }
    
    /**
     * Get the NBT on the ItemStack
     * @param stack the given ItemStack
     * @return <code>CompoundNBT</code> if the stack is the compass and have NBT;
     *         <code>null</code> otherwise
     */
    @Nullable
    public static CompoundTag getItemTag(@Nonnull ItemStack stack) {
        return isStackItemStructuresCompass(stack) ? stack.getTag() : null;
    }
    
    /**
     * Remove a specific tag on the ItemStack
     * @param stack the given ItemStack
     * @param TAG_NAME the name of the tag
     */
    public static void removeTag(@Nonnull ItemStack stack, String TAG_NAME) {
        if (stack.getItem() == ItemRegistry.STRUCTURES_COMPASS.get()) {
            CompoundTag tag = stack.getTag();
            if (tag == null) return;
            tag.remove(TAG_NAME);
            stack.setTag(tag);
        }
    }
    
    /**
     * Get the ItemStack of the given item from the player
     * @param player the player entity
     * @param item the item to get
     * @return the ItemStack of the given item
     */
    public static ItemStack getHeldItem(Player player, Item item) {
        if (player == null) return ItemStack.EMPTY;
        if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() == item)
            return player.getMainHandItem();
        else if (!player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() == item)
            return player.getOffhandItem();
        return ItemStack.EMPTY;
    }
    
    /**
     * Get the compass held by the player
     * @param player the player entity
     * @return the ItemStack of the compass
     */
    public static ItemStack getHeldStructuresCompass(Player player) {
        return getHeldItem(player, ItemRegistry.STRUCTURES_COMPASS.get());
    }
}
