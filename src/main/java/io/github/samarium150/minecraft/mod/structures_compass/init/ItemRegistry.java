package io.github.samarium150.minecraft.mod.structures_compass.init;

import io.github.samarium150.minecraft.mod.structures_compass.item.StructuresCompassItem;
import io.github.samarium150.minecraft.mod.structures_compass.util.GeneralUtils;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
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
