package com.github.samarium150.structurescompass.event;

import com.github.samarium150.structurescompass.network.StructuresCompassNetwork;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

/**
 * Handler for all common events
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonEventHandler {
    
    private CommonEventHandler() { }
    
    /**
     * Handle common setup
     * @param event FMLCommonSetupEvent
     */
    @SubscribeEvent
    public static void onCommonSetup(@Nonnull final FMLCommonSetupEvent event) {
        StructuresCompassNetwork.init();
    }
}
