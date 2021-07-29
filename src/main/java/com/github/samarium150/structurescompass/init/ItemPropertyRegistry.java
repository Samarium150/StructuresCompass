package com.github.samarium150.structurescompass.init;

import com.github.samarium150.structurescompass.item.StructuresCompassItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nonnull;

/**
 * Registry of properties of items in the mod
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ItemPropertyRegistry {
    
    private ItemPropertyRegistry() { }
    
    /**
     * Register all item properties
     * @param event FMLClientSetupEvent
     */
    @SubscribeEvent
    public static void register(@Nonnull final FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemModelsProperties.register(
            ItemRegistry.STRUCTURES_COMPASS.get(),
            new ResourceLocation("angle"),
            new StructuresCompassItemPropertyGetter()
        ));
    }
}
