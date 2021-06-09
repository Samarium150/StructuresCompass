package com.github.samarium150.structurescompass.util;

import com.github.samarium150.structurescompass.config.HUDPosition;
import com.github.samarium150.structurescompass.config.StructuresCompassConfig;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Utilities related to rendering
 * <p>
 * This class is adapted from
 * <a href="https://github.com/MattCzyr/NaturesCompass" target="_blank">NaturesCompass</a>
 * which is under
 * <a href="https://creativecommons.org/licenses/by-nc-sa/4.0" target="_blank">
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License
 * </a>.
 */
@OnlyIn(Dist.CLIENT)
public abstract class RenderUtils {
    
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final FontRenderer fontRenderer = minecraft.fontRenderer;
    
    private RenderUtils() { }
    
    private static void drawStringLeft(String string, int x, int y, int color) {
        fontRenderer.drawString(string, x, y, color);
    }
    
    private static void drawStringRight(String string, int x, int y, int color) {
        fontRenderer.drawString(string, x, y, color);
    }
    
    public static void drawConfiguredStringOnHUD(
        String string,
        int xOffset, int yOffset, int color, int relLineOffset
    ) {
        yOffset += (relLineOffset + StructuresCompassConfig.overlayLineOffset.get()) * 9;
        if (StructuresCompassConfig.hudPosition.get() == HUDPosition.LEFT)
            drawStringLeft(
                string,
                xOffset + StructuresCompassConfig.xOffset.get() - 5,
                yOffset + StructuresCompassConfig.yOffset.get() - 14, color
            );
        else
            drawStringRight(
                string,
                minecraft.getMainWindow().getScaledWidth()
                    - fontRenderer.getStringWidth(string) - xOffset - StructuresCompassConfig.xOffset.get() + 5,
                yOffset + StructuresCompassConfig.yOffset.get() - 14, color
            );
    }
    
    public static void updateBuffer(@Nonnull BufferBuilder buffer, int startX, int startY, int endX, int endY) {
        buffer.begin(7, DefaultVertexFormats.POSITION);
        buffer.pos(startX, endY, 0.0D).endVertex();
        buffer.pos(endX, endY, 0.0D).endVertex();
        buffer.pos(endX, startY, 0.0D).endVertex();
        buffer.pos(startX, startY, 0.0D).endVertex();
    }
    
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right)
            left = GeneralUtils.swap(right, right = left);
        if (top < bottom)
            top = GeneralUtils.swap(bottom, bottom = top);
        
        final float red = (float) (color >> 16 & 255) / 255.0F;
        final float green = (float) (color >> 8 & 255) / 255.0F;
        final float blue = (float) (color & 255) / 255.0F;
        final float alpha = (float) (color >> 24 & 255) / 255.0F;
        
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        );
        
        RenderSystem.color4f(red, green, blue, alpha);
        
        updateBuffer(buffer, left, top, right, bottom);
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
