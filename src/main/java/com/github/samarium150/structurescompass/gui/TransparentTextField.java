package com.github.samarium150.structurescompass.gui;

import com.github.samarium150.structurescompass.util.GeneralUtils;
import com.github.samarium150.structurescompass.util.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Class of the transparent style text field
 * <p>
 * This class is adapted from
 * <a href="https://github.com/MattCzyr/NaturesCompass" target="_blank">NaturesCompass</a>
 * which is under
 * <a href="https://creativecommons.org/licenses/by-nc-sa/4.0" target="_blank">
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License
 * </a>.
 */
@OnlyIn(Dist.CLIENT)
public class TransparentTextField extends TextFieldWidget {
    
    private final FontRenderer fontRenderer;
    private String label;
    private int labelColor = 0x808080;
    
    private boolean pseudoIsEnabled = true;
    private boolean pseudoEnableBackgroundDrawing = true;
    private int pseudoMaxStringLength = 32;
    private int pseudoLineScrollOffset;
    private int pseudoEnabledColor = 14737632;
    private int pseudoDisabledColor = 7368816;
    private int pseudoCursorCounter;
    private int pseudoSelectionEnd;
    
    public TransparentTextField(FontRenderer fontRenderer, int x, int y, int width, int height, String label) {
        super(fontRenderer, x, y, width, height, label);
        this.fontRenderer = fontRenderer;
        this.label = label;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (getVisible()) {
            if (pseudoEnableBackgroundDrawing) {
                final int color = (int) (255.0F * 1.7F);
                RenderUtils.drawRect(x, y, x + width, y + height, color / 2 << 24);
            }
            boolean showLabel = !isFocused() && getText().isEmpty();
            int i = showLabel ? labelColor : (pseudoIsEnabled ? pseudoEnabledColor : pseudoDisabledColor);
            int j = getCursorPosition() - pseudoLineScrollOffset;
            int k = pseudoSelectionEnd - pseudoLineScrollOffset;
            String text = showLabel ? label : getText();
            String s = fontRenderer.trimStringToWidth(text.substring(pseudoLineScrollOffset), getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = isFocused() && pseudoCursorCounter / 6 % 2 == 0 && flag;
            int l = pseudoEnableBackgroundDrawing ? x + 4 : x;
            int i1 = pseudoEnableBackgroundDrawing ? y + (height - 8) / 2 : y;
            int j1 = l;
            
            if (k > s.length()) {
                k = s.length();
            }
            
            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = fontRenderer.drawStringWithShadow(s1, (float) l, (float) i1, i);
            }
            
            boolean flag2 = getCursorPosition() < getText().length() || getText().length() >= pseudoMaxStringLength;
            int k1 = j1;
            
            if (!flag) {
                k1 = j > 0 ? l + width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }
            
            if (!s.isEmpty() && flag && j < s.length()) {
                fontRenderer.drawStringWithShadow(s.substring(j), (float) j1, (float) i1, i);
            }
            
            if (flag1) {
                if (flag2) {
                    RenderUtils.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + fontRenderer.FONT_HEIGHT, -3092272);
                } else {
                    fontRenderer.drawStringWithShadow("_", (float) k1, (float) i1, i);
                }
            }
            
            if (k != j) {
                int l1 = l + fontRenderer.getStringWidth(s.substring(0, k));
                drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + fontRenderer.FONT_HEIGHT);
            }
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        pseudoIsEnabled = enabled;
    }
    
    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        pseudoEnabledColor = color;
    }
    
    @Override
    public void setDisabledTextColour(int color) {
        super.setDisabledTextColour(color);
        pseudoDisabledColor = color;
    }
    
    @Override
    public void setFocused(boolean isFocused) {
        if (isFocused && !isFocused()) {
            pseudoCursorCounter = 0;
        }
        super.setFocused(isFocused);
    }
    
    @Override
    public void setEnableBackgroundDrawing(boolean enableBackgroundDrawing) {
        super.setEnableBackgroundDrawing(enableBackgroundDrawing);
        pseudoEnableBackgroundDrawing = enableBackgroundDrawing;
    }
    
    @Override
    public void setMaxStringLength(int length) {
        super.setMaxStringLength(length);
        pseudoMaxStringLength = length;
    }
    
    @Override
    public void tick() {
        super.tick();
        pseudoCursorCounter++;
    }
    
    @Override
    public void setSelectionPos(int position) {
        super.setSelectionPos(position);
        int i = getText().length();
        pseudoSelectionEnd = MathHelper.clamp(position, 0, i);
        if (fontRenderer != null) {
            if (pseudoLineScrollOffset > i) {
                pseudoLineScrollOffset = i;
            }
            
            int j = getAdjustedWidth();
            String s = fontRenderer.trimStringToWidth(getText().substring(this.pseudoLineScrollOffset), j);
            int k = s.length() + pseudoLineScrollOffset;
            if (pseudoSelectionEnd == pseudoLineScrollOffset) {
                pseudoLineScrollOffset -= fontRenderer.trimStringToWidth(getText(), j, true).length();
            }
            
            if (pseudoSelectionEnd > k) {
                pseudoLineScrollOffset += pseudoSelectionEnd - k;
            } else if (pseudoSelectionEnd <= pseudoLineScrollOffset) {
                pseudoLineScrollOffset -= pseudoLineScrollOffset - pseudoSelectionEnd;
            }
            
            pseudoLineScrollOffset = MathHelper.clamp(pseudoLineScrollOffset, 0, i);
        }
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }
    
    private void drawSelectionBox(int startX, int startY, int endX, int endY) {
        if (startX < endX)
            startX = GeneralUtils.swap(endX, endX = startX);
        if (startY < endY)
            startY = GeneralUtils.swap(endY, endY = startY);
        if (endX > x + width)
            endX = x + width;
        if (startX > x + width)
            startX = x + width;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        RenderUtils.updateBuffer(buffer, startX, startY, endX, endY);
        tessellator.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }
}
