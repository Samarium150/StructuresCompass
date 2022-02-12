package io.github.samarium150.minecraft.mod.structures_compass.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
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
        Minecraft.getInstance().setScreen(
            new StructuresCompassScreen(stack)
        );
    }
}
