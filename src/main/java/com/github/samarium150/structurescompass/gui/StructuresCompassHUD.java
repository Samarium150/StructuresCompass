package com.github.samarium150.structurescompass.gui;

import com.github.samarium150.structurescompass.config.StructuresCompassConfig;
import com.github.samarium150.structurescompass.init.ItemRegistry;
import com.github.samarium150.structurescompass.item.StructuresCompassItem;
import com.github.samarium150.structurescompass.util.RenderUtils;
import com.github.samarium150.structurescompass.util.StructureUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

/**
 * The HUD of the compass
 */
public final class StructuresCompassHUD extends AbstractGui {
    
    private static final Minecraft minecraft = Minecraft.getInstance();
    private final MatrixStack matrixStack;
    
    /**
     * Initializer of the HUD
     * @param matrixStack MatrixStack from RenderGameOverlayEvent
     * @see net.minecraftforge.client.event.RenderGameOverlayEvent
     */
    public StructuresCompassHUD(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }
    
    /**
     * Render the HUD
     * <p>
     * This method is adapted from
     * <a href="https://github.com/MattCzyr/NaturesCompass" target="_blank">NaturesCompass</a>
     * which is under
     * <a href="https://creativecommons.org/licenses/by-nc-sa/4.0" target="_blank">
     * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License
     * </a>.
     */
    public void render() {
        
        if (StructuresCompassConfig.HUD_Level.get() != 0 &&
                (minecraft.currentScreen == null || (StructuresCompassConfig.displayWithChatOpen.get()
                                                            && minecraft.currentScreen instanceof ChatScreen))) {
            final PlayerEntity player = minecraft.player;
            final ItemStack stack;
            assert player != null;
            if (!player.getHeldItemMainhand().isEmpty()
                    && player.getHeldItemMainhand().getItem() == ItemRegistry.STRUCTURES_COMPASS.get())
                stack = player.getHeldItemMainhand();
            else if (!player.getHeldItemOffhand().isEmpty()
                         && player.getHeldItemOffhand().getItem() == ItemRegistry.STRUCTURES_COMPASS.get())
                stack = player.getHeldItemOffhand();
            else return;
            if (stack.getItem() instanceof StructuresCompassItem) {
                String feature = StructuresCompassItem.getStructureName(stack);
                String dim = StructuresCompassItem.getDimension(stack);
                BlockPos pos = StructuresCompassItem.getPos(stack);
                int relLineOffset = 0;
                if (feature == null) return;
                if (pos == null || dim == null) {
                    RenderUtils.drawConfiguredStringOnHUD(
                        matrixStack, I18n.format("string.structurescompass.hud_not_found"),
                        5, 5, 0xAA2116, relLineOffset
                    );
                } else {
                    RenderUtils.drawConfiguredStringOnHUD(
                        matrixStack, I18n.format("string.structurescompass.hud_structure"),
                        5, 5, 0xFFFFFF, relLineOffset);
                    RenderUtils.drawConfiguredStringOnHUD(
                        matrixStack, StructureUtils.getLocalizedStructureName(feature),
                        5, 5, 0xAAAAAA, ++relLineOffset);
                    relLineOffset++;
            
                    RenderUtils.drawConfiguredStringOnHUD(
                        matrixStack, I18n.format("string.structurescompass.hud_dim"),
                        5, 5, 0xFFFFFF, ++relLineOffset
                    );
                    RenderUtils.drawConfiguredStringOnHUD(
                        matrixStack, StructureUtils.getLocalizedDimensionName(dim),
                        5, 5, 0xAAAAAA, ++relLineOffset
                    );
                    relLineOffset++;

                    if (StructuresCompassConfig.HUD_Level.get() == 3) {
                        String temp = pos.getY() == 0 ? ", X, " : ", " + pos.getY() + ", ";
                        temp = I18n.format("string.structurescompass.hud_pos") + " [" + pos.getX() + temp + pos.getZ() + "]";
                        RenderUtils.drawConfiguredStringOnHUD(
                            matrixStack, temp,
                            5, 5, 0x4AFF4A, ++relLineOffset
                        );
                    }
            
                    if (player.getEntityWorld().getDimensionKey().getLocation().toString().equals(dim)) {
                
                        Vector3d dis = StructureUtils.getDistance(pos, player);
                        double disX = dis.getX();
                        double disY = dis.getY();
                        double disZ = dis.getZ();
                        double distance = (double) Math.round(Math.sqrt(disX * disX + disY * disY + disZ * disZ) * 100) / 100;
                
                        if (StructuresCompassConfig.HUD_Level.get() == 3 && disX > StructuresCompassConfig.closeEnough.get())
                            RenderUtils.drawConfiguredStringOnHUD(
                                matrixStack, I18n.format("string.structurescompass.hud_east") + disX,
                                5, 5, 0xFFFFFF, ++relLineOffset
                            );
                        else if (StructuresCompassConfig.HUD_Level.get() == 3 && disX < -StructuresCompassConfig.closeEnough.get())
                            RenderUtils.drawConfiguredStringOnHUD(
                                matrixStack, I18n.format("string.structurescompass.hud_west") + -disX,
                                5, 5, 0xFFFFFF, ++relLineOffset
                            );
                
                        if (StructuresCompassConfig.HUD_Level.get() == 3 && disZ > StructuresCompassConfig.closeEnough.get())
                            RenderUtils.drawConfiguredStringOnHUD(
                                matrixStack, I18n.format("string.structurescompass.hud_south") + disZ,
                                5, 5, 0xFFFFFF, ++relLineOffset
                            );
                        else if (StructuresCompassConfig.HUD_Level.get() == 3 && disZ < -StructuresCompassConfig.closeEnough.get())
                            RenderUtils.drawConfiguredStringOnHUD(
                                matrixStack, I18n.format("string.structurescompass.hud_north") + -disZ,
                                5, 5, 0xFFFFFF, ++relLineOffset
                            );
                
                        if (StructuresCompassConfig.HUD_Level.get() == 3 && disY > StructuresCompassConfig.closeEnough.get())
                            RenderUtils.drawConfiguredStringOnHUD(
                                matrixStack, I18n.format("string.structurescompass.hud_up") + disY,
                                5, 5, 0xFFFFFF, ++relLineOffset
                            );
                        else if (StructuresCompassConfig.HUD_Level.get() == 3 && disY < -StructuresCompassConfig.closeEnough.get())
                            RenderUtils.drawConfiguredStringOnHUD(
                                matrixStack, I18n.format("string.structurescompass.hud_down") + -disY,
                                5, 5, 0xFFFFFF, ++relLineOffset
                            );
                
                        if ((StructuresCompassConfig.HUD_Level.get() == 3 && relLineOffset > 6) ||
                            (StructuresCompassConfig.HUD_Level.get() >= 2 && distance > StructuresCompassConfig.closeEnough.get())) {
                            relLineOffset++;
                            RenderUtils.drawConfiguredStringOnHUD(
                                matrixStack, I18n.format("string.structurescompass.hud_distance") + distance,
                                5, 5, 0xFFC20E, ++relLineOffset
                            );
                        }
                    } else {
                        relLineOffset++;
                        RenderUtils.drawConfiguredStringOnHUD(
                            matrixStack, I18n.format("string.structurescompass.hud_wrong_dim"),
                            5, 5, 0xAA2116, ++relLineOffset
                        );
                    }
                }
            }
        }
    }
}
