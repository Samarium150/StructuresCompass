package com.github.samarium150.structurescompass.gui;

import com.github.samarium150.structurescompass.util.GeneralUtils;
import com.github.samarium150.structurescompass.util.RenderUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

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
    private ITextComponent label;
    private int labelColor = 0x808080;
    
    private boolean pseudoIsEnabled = true;
    private boolean pseudoEnableBackgroundDrawing = true;
    private int pseudoMaxStringLength = 32;
    private int pseudoLineScrollOffset;
    private int pseudoEnabledColor = 14737632;
    private int pseudoDisabledColor = 7368816;
    private int pseudoCursorCounter;
    private int pseudoSelectionEnd;
    
    public TransparentTextField(FontRenderer fontRenderer, int x, int y, int width, int height, ITextComponent label) {
        super(fontRenderer, x, y, width, height, label);
        this.fontRenderer = fontRenderer;
        this.label = label;
    }
    
    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (isVisible()) {
            if (pseudoEnableBackgroundDrawing) {
                final int color = (int) (255.0F * 1.7F);
                RenderUtils.drawRect(x, y, x + width, y + height, color / 2 << 24);
            }
            boolean showLabel = !isFocused() && getValue().isEmpty();
            int i = showLabel ? labelColor : (pseudoIsEnabled ? pseudoEnabledColor : pseudoDisabledColor);
            int j = getCursorPosition() - pseudoLineScrollOffset;
            int k = pseudoSelectionEnd - pseudoLineScrollOffset;
            String text = showLabel ? label.getString() : getValue();
            String s = fontRenderer.plainSubstrByWidth(text.substring(pseudoLineScrollOffset), getWidth());
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
                j1 = fontRenderer.drawShadow(matrixStack, s1, (float) l, (float) i1, i);
            }
            
            boolean flag2 = getCursorPosition() < getValue().length() || getValue().length() >= pseudoMaxStringLength;
            int k1 = j1;
            
            if (!flag) {
                k1 = j > 0 ? l + width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }
            
            if (!s.isEmpty() && flag && j < s.length()) {
                fontRenderer.drawShadow(matrixStack, s.substring(j), (float) j1, (float) i1, i);
            }
            
            if (flag1) {
                if (flag2) {
                    RenderUtils.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + fontRenderer.lineHeight, -3092272);
                } else {
                    fontRenderer.drawShadow(matrixStack, "_", (float) k1, (float) i1, i);
                }
            }
            
            if (k != j) {
                int l1 = l + fontRenderer.width(s.substring(0, k));
                drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + fontRenderer.lineHeight);
            }
        }
    }
    
    @Override
    public void setEditable(boolean enabled) {
        super.setEditable(enabled);
        pseudoIsEnabled = enabled;
    }
    
    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        pseudoEnabledColor = color;
    }
    
    @Override
    public void setTextColorUneditable(int color) {
        super.setTextColorUneditable(color);
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
    public void setBordered(boolean enableBackgroundDrawing) {
        super.setBordered(enableBackgroundDrawing);
        pseudoEnableBackgroundDrawing = enableBackgroundDrawing;
    }
    
    @Override
    public void setMaxLength(int length) {
        super.setMaxLength(length);
        pseudoMaxStringLength = length;
    }
    
    @Override
    public void tick() {
        super.tick();
        pseudoCursorCounter++;
    }
    
    @Override
    public void setHighlightPos(int position) {
        super.setHighlightPos(position);
        int i = getValue().length();
        pseudoSelectionEnd = MathHelper.clamp(position, 0, i);
        if (fontRenderer != null) {
            if (pseudoLineScrollOffset > i) {
                pseudoLineScrollOffset = i;
            }
            
            int j = getInnerWidth();
            String s = fontRenderer.plainSubstrByWidth(getValue().substring(this.pseudoLineScrollOffset), j, false);
            int k = s.length() + pseudoLineScrollOffset;
            if (pseudoSelectionEnd == pseudoLineScrollOffset) {
                pseudoLineScrollOffset -= fontRenderer.plainSubstrByWidth(getValue(), j, true).length();
            }
            
            if (pseudoSelectionEnd > k) {
                pseudoLineScrollOffset += pseudoSelectionEnd - k;
            } else if (pseudoSelectionEnd <= pseudoLineScrollOffset) {
                pseudoLineScrollOffset -= pseudoLineScrollOffset - pseudoSelectionEnd;
            }
            
            pseudoLineScrollOffset = MathHelper.clamp(pseudoLineScrollOffset, 0, i);
        }
    }
    
    public void setLabel(ITextComponent label) {
        this.label = label;
    }
    
    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }
    
    @SuppressWarnings("deprecation")
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
        BufferBuilder buffer = tessellator.getBuilder();
    
        // Annotated as deprecated but no replacement
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        RenderUtils.updateBuffer(buffer, startX, startY, endX, endY);
        tessellator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }
}
