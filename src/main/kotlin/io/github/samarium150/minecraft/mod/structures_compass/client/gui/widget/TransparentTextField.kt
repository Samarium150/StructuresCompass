/*
 * Copyright (c) 2022 Samarium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.html>.
 */
package io.github.samarium150.minecraft.mod.structures_compass.client.gui.widget

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import io.github.samarium150.minecraft.mod.structures_compass.client.util.drawRect
import io.github.samarium150.minecraft.mod.structures_compass.client.util.update
import io.github.samarium150.minecraft.mod.structures_compass.util.Rect
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.render.Tessellator
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper

@Environment(EnvType.CLIENT)
class TransparentTextField(
    private val textRenderer: TextRenderer?,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private var label: Text
) : TextFieldWidget(textRenderer, x, y, width, height, label) {

    private var labelColor = 0x808080
    private var pseudoEditable = true
    private var pseudoEnableBackgroundDrawing = true
    private var pseudoMaxLength = 32
    private var pseudoLineScrollOffset = 0
    private var pseudoEditableColor = 14737632
    private var pseudoDisabledColor = 7368816
    private var pseudoCursorCounter = 0
    private var pseudoSelectionEnd = 0

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (isVisible) {
            if (pseudoEnableBackgroundDrawing) {
                val color = (255.0f * 0.55f).toInt()
                drawRect(x, y, x + width, y + height, color / 2 shl 24)
            }
            val showLabel = !isFocused && text.isEmpty()
            val i = if (showLabel) labelColor else if (pseudoEditable) pseudoEditableColor else pseudoDisabledColor
            val j = cursor - pseudoLineScrollOffset
            var k = pseudoSelectionEnd - pseudoLineScrollOffset
            val text = if (showLabel) label.string else text
            val s = textRenderer!!.trimToWidth(text.substring(pseudoLineScrollOffset), getWidth())
            val flag = j >= 0 && j <= s.length
            val flag1 = isFocused && pseudoCursorCounter / 6 % 2 == 0 && flag
            val l = if (pseudoEnableBackgroundDrawing) x + 4 else x
            val i1 = if (pseudoEnableBackgroundDrawing) y + (height - 8) / 2 else y
            var j1 = l
            if (k > s.length) {
                k = s.length
            }
            if (s.isNotEmpty()) {
                val s1 = if (flag) s.substring(0, j) else s
                j1 = textRenderer.drawWithShadow(matrixStack, s1, l.toFloat(), i1.toFloat(), i)
            }
            val flag2 = cursor < getText().length || getText().length >= pseudoMaxLength
            var k1 = j1
            if (!flag)
                k1 = if (j > 0) l + width else l
            else if (flag2) {
                k1 = j1 - 1
                --j1
            }
            if (s.isNotEmpty() && flag && j < s.length)
                textRenderer.drawWithShadow(matrixStack, s.substring(j), j1.toFloat(), i1.toFloat(), i)

            if (flag1)
                if (flag2)
                    drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + textRenderer.fontHeight, -3092272)
                else
                    textRenderer.drawWithShadow(matrixStack, "_", k1.toFloat(), i1.toFloat(), i)

            if (k != j) {
                val l1 = l + textRenderer.getWidth(s.substring(0, k))
                drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + textRenderer.fontHeight)
            }
        }
    }

    override fun setEditable(editable: Boolean) {
        super.setEditable(editable)
        pseudoEditable = editable
    }

    override fun setEditableColor(color: Int) {
        super.setEditableColor(color)
        pseudoEditableColor = color
    }

    override fun setUneditableColor(color: Int) {
        super.setUneditableColor(color)
        pseudoDisabledColor = color
    }

    public override fun setFocused(isFocused: Boolean) {
        if (isFocused && !isFocused()) {
            pseudoCursorCounter = 0
        }
        super.setFocused(isFocused)
    }

    override fun setDrawsBackground(drawsBackground: Boolean) {
        super.setDrawsBackground(drawsBackground)
        pseudoEnableBackgroundDrawing = drawsBackground
    }

    override fun setMaxLength(length: Int) {
        super.setMaxLength(length)
        pseudoMaxLength = length
    }

    override fun tick() {
        super.tick()
        pseudoCursorCounter++
    }

    override fun setSelectionEnd(position: Int) {
        super.setSelectionEnd(position)
        val i = text.length
        pseudoSelectionEnd = MathHelper.clamp(position, 0, i)
        if (textRenderer != null) {
            if (pseudoLineScrollOffset > i) {
                pseudoLineScrollOffset = i
            }
            val j = innerWidth
            val s = textRenderer.trimToWidth(text.substring(pseudoLineScrollOffset), j, false)
            val k = s.length + pseudoLineScrollOffset
            if (pseudoSelectionEnd == pseudoLineScrollOffset) {
                pseudoLineScrollOffset -= textRenderer.trimToWidth(text, j, true).length
            }
            if (pseudoSelectionEnd > k) {
                pseudoLineScrollOffset += pseudoSelectionEnd - k
            } else if (pseudoSelectionEnd <= pseudoLineScrollOffset) {
                pseudoLineScrollOffset -= pseudoLineScrollOffset - pseudoSelectionEnd
            }
            pseudoLineScrollOffset = MathHelper.clamp(pseudoLineScrollOffset, 0, i)
        }
    }

    @Suppress("unused")
    fun setLabel(label: Text) {
        this.label = label
    }

    @Suppress("unused")
    fun setLabelColor(labelColor: Int) {
        this.labelColor = labelColor
    }

    private fun drawSelectionBox(startX: Int, startY: Int, endX: Int, endY: Int) {
        val (sanitizedStartX, sanitizedStartY, sanitizedEndX, sanitizedEndY) =
            Rect(startX, startY, endX, endY).sanitize()
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer
        RenderSystem.setShaderColor(0.0f, 0.0f, 255.0f, 255.0f)
        RenderSystem.disableTexture()
        RenderSystem.enableColorLogicOp()
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE)
        buffer.update(sanitizedStartX, sanitizedStartY, sanitizedEndX, sanitizedEndY)
        tessellator.draw()
        RenderSystem.disableColorLogicOp()
        RenderSystem.enableTexture()
    }
}
