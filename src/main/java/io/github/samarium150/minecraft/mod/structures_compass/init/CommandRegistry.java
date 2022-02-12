package io.github.samarium150.minecraft.mod.structures_compass.init;

import io.github.samarium150.minecraft.mod.structures_compass.command.*;
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
