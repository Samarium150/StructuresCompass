package com.github.samarium150.structurescompass.init;

import com.github.samarium150.structurescompass.item.StructuresCompassItem;
import com.github.samarium150.structurescompass.util.GeneralUtils;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The registry of items in the mod
 */
public final class ItemRegistry {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GeneralUtils.MOD_ID);
    public static final RegistryObject<Item> STRUCTURES_COMPASS = ITEMS.register(StructuresCompassItem.NAME, StructuresCompassItem::new);
    
    private ItemRegistry() { }
}
