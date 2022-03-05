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

import io.github.samarium150.minecraft.mod.structures_compass.client.util.drawRect
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class TransparentButton(x: Int, y: Int, width: Int, height: Int, label: Text?, onPress: PressAction?) :
    ButtonWidget(x, y, width, height, label, onPress) {

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (visible) {
            val mc = MinecraftClient.getInstance()
            var state = 2f
            if (!active) {
                state = 5f
            } else if (isHovered) {
                state = 4f
            }
            val f = state / 2 * 0.9f + 0.1f
            val color = (255.0f * f).toInt()
            drawRect(x, y, x + width, y + height, color / 2 shl 24)
            drawCenteredText(
                matrixStack, mc.textRenderer,
                message, x + width / 2, y + (height - 8) / 2, 0xffffff
            )
        }
    }

    @Suppress("unused")
    fun getHoverState(mouseOver: Boolean): Int {
        var state = 2
        if (!active) {
            state = 5
        } else if (mouseOver) {
            state = 4
        }
        return state
    }
}
