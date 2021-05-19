package com.github.samarium150.structurescompass.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The GUI of the compass
 */
@OnlyIn(Dist.CLIENT)
public final class StructuresCompassGUI {
    
    private StructuresCompassGUI() { }
    
    /**
     * Open the GUI for the player
     * @param stack ItemStack
     */
    public static void openGUI(ItemStack stack) {
        Minecraft.getInstance().displayGuiScreen(
            new StructuresCompassScreen(stack)
        );
    }
}
