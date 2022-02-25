package com.github.samarium150.structurescompass.client;

import com.github.samarium150.structurescompass.gui.StructuresCompassHUD;
import com.github.samarium150.structurescompass.init.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

/**
 * Handler for all client events
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public final class ClientEventHandler {
    
    private static final Minecraft minecraft = Minecraft.getInstance();
    
    private ClientEventHandler() { }
    
    @SubscribeEvent
    public static void onRenderOverlay(@Nonnull final RenderGameOverlayEvent event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        if (minecraft.level == null ||
                minecraft.player == null ||
                !minecraft.player.isHolding(ItemRegistry.STRUCTURES_COMPASS.get()) ||
                minecraft.options.hideGui)
            return;

        new StructuresCompassHUD(event.getMatrixStack()).render();
    }
}
