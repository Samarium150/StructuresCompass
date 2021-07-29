package com.github.samarium150.structurescompass.gui;

import com.github.samarium150.structurescompass.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Class of the transparent style button
 * <p>
 * This class is adapted from
 * <a href="https://github.com/MattCzyr/NaturesCompass" target="_blank">NaturesCompass</a>
 * which is under
 * <a href="https://creativecommons.org/licenses/by-nc-sa/4.0" target="_blank">
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License
 * </a>.
 */
@OnlyIn(Dist.CLIENT)
public class TransparentButton extends Button {
    
    public TransparentButton(int x, int y, int width, int height, Component title, OnPress pressedAction) {
        super(x, y, width, height, title, pressedAction);
    }
    
    protected int getHoverState(boolean mouseOver) {
        return (!active) ? 5 : (mouseOver) ? 4 : 2;
    }
    
    /**
     * draw centered string without shadow
     * @param matrixStack MatrixStack
     * @param fontRenderer FontRenderer
     * @param text ITextComponent
     * @param x x position
     * @param y y position
     * @param color color of the text
     */
    public static void drawCenteredString(
        @Nonnull PoseStack matrixStack, @Nonnull Font fontRenderer, @Nonnull Component text,
        int x, int y, int color
    ) {
        fontRenderer.draw(
            matrixStack, text,
            (float)(x - fontRenderer.width(text.getVisualOrderText()) / 2), (float)y, color
        );
    }
    
    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            Minecraft minecraft = Minecraft.getInstance();
            float state = getHoverState(isHovered());
            final float f = state / 2 * 0.9F + 0.1F;
            final int color = (int) (255.0F * f);
        
            RenderUtils.drawRect(x, y, x + width, y + height, color / 2 << 24);
            TransparentButton.drawCenteredString(
                matrixStack, minecraft.font, getMessage(),
                x + width / 2, y + (height - 8) / 2, 0xffffff
            );
        }
    }
}
