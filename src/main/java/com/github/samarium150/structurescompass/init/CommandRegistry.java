package com.github.samarium150.structurescompass.init;

import com.github.samarium150.structurescompass.command.*;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

/**
 * The registry of commands in the mod
 */
@Mod.EventBusSubscriber
public final class CommandRegistry {
    
    private CommandRegistry() { }
    
    /**
     * Register commands
     * @param event RegisterCommandsEvent
     * @see RegisterCommandsEvent
     */
    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void register(@Nonnull final RegisterCommandsEvent event) {
        GetCompass.register(event.getDispatcher());
    }
}
