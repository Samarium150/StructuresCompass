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
package io.github.samarium150.minecraft.mod.structures_compass.client.util

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import io.github.samarium150.minecraft.mod.structures_compass.util.Rect
import io.github.samarium150.minecraft.mod.structures_compass.util.minecraftClient
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack

@Environment(EnvType.CLIENT)
fun TextRenderer.drawConfiguredStringOnHud(
    matrixStack: MatrixStack,
    string: String, xOffset: Int, yOffset: Int,
    color: Int, relLineOffset: Int
) {
    // TODO: replace 1 with StructuresCompassConfig.overlayLineOffset
    val configuredYOffset = yOffset + (relLineOffset + 1) * 9
    // TODO: replace ture with StructuresCompassConfig.hudPosition
    if (true)
        draw(
            matrixStack,
            string,
            (xOffset + 7 - 5).toFloat(), // TODO: replace 7 with StructuresCompassConfig.xOffset
            (configuredYOffset + 16 - 14).toFloat(), // TODO: replace 16 with StructuresCompassConfig.yOffset
            color
        )
    else
        draw(
            matrixStack,
            string,
            // TODO: replace 7 with StructuresCompassConfig.xOffset
            (minecraftClient.window.scaledWidth - getWidth(string) - xOffset - 7 + 5).toFloat(),
            (configuredYOffset + 0 - 14).toFloat(), // TODO: replace 0 with StructuresCompassConfig.yOffset
            color
        )
}

@Environment(EnvType.CLIENT)
fun BufferBuilder.update(startX: Int, startY: Int, endX: Int, endY: Int) {
    begin(7, VertexFormats.POSITION)
    vertex(startX.toDouble(), endY.toDouble(), 0.0).next()
    vertex(endX.toDouble(), endY.toDouble(), 0.0).next()
    vertex(endX.toDouble(), startY.toDouble(), 0.0).next()
    vertex(startX.toDouble(), startY.toDouble(), 0.0).next()
}

@Environment(EnvType.CLIENT)
fun drawRect(left: Int, top: Int, right: Int, bottom: Int, color: Int) {

    val (sanitizedLeft, sanitizedTop, sanitizedRight, sanitizedBottom) = Rect(left, top, right, bottom).sanitize()

    val red = (color shr 16 and 255).toFloat() / 255.0f
    val green = (color shr 8 and 255).toFloat() / 255.0f
    val blue = (color and 255).toFloat() / 255.0f
    val alpha = (color shr 24 and 255).toFloat() / 255.0f

    val tessellator = Tessellator.getInstance()
    val buffer = tessellator.buffer

    RenderSystem.enableBlend()
    RenderSystem.disableTexture()
    RenderSystem.blendFuncSeparate(
        GlStateManager.SrcFactor.SRC_ALPHA,
        GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
        GlStateManager.SrcFactor.ONE,
        GlStateManager.DstFactor.ZERO
    )
    @Suppress("DEPRECATION")
    RenderSystem.color4f(red, green, blue, alpha)
    buffer.update(sanitizedLeft, sanitizedTop, sanitizedRight, sanitizedBottom)
    tessellator.draw()
    RenderSystem.enableTexture()
    RenderSystem.disableBlend()
}
