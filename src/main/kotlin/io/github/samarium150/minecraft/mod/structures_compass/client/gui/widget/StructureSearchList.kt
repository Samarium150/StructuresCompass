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

import io.github.samarium150.minecraft.mod.structures_compass.client.gui.StructuresCompassScreen
import io.github.samarium150.minecraft.mod.structures_compass.client.util.drawRect
import io.github.samarium150.minecraft.mod.structures_compass.util.getIdentifier
import io.github.samarium150.minecraft.mod.structures_compass.util.minecraftClient
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.world.gen.feature.StructureFeature
import java.util.*

@Environment(EnvType.CLIENT)
class StructureSearchList(
    val screen: StructuresCompassScreen,
    width: Int,
    height: Int,
    top: Int,
    bottom: Int,
    slotHeight: Int
) : AlwaysSelectedEntryListWidget<StructureSearchEntry>(
    minecraftClient, width, height, top, bottom, slotHeight
) {
    private val map = mutableMapOf<Identifier, StructureSearchEntry>()

    init {
        refresh()
    }

    override fun getScrollbarPositionX(): Int {
        return super.getScrollbarPositionX() + 20
    }

    override fun getRowWidth(): Int {
        return super.getRowWidth() + 50
    }

    override fun isSelectedEntry(slotIndex: Int): Boolean {
        return if (slotIndex >= 0 && slotIndex < children().size) children()[slotIndex].equals(selectedOrNull) else false
    }

    private fun getRowBottom(entryIndex: Int): Int {
        return getRowTop(entryIndex) + itemHeight
    }

    override fun renderList(
        matrixStack: MatrixStack?,
        x: Int,
        y: Int,
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float
    ) {
        for (i in 0 until entryCount) {
            val rowTop = getRowTop(i)
            val rowBottom = getRowBottom(i)
            if (rowBottom + height >= top && rowTop <= bottom) {
                val entry = getEntry(i)
                val h = itemHeight - 4
                val w = rowWidth
                if (isSelectedEntry(i)) {
                    val insideLeft: Int = left + width / 2 - rowWidth / 2 + 2
                    drawRect(
                        insideLeft - 4,
                        rowTop - 4,
                        insideLeft + rowWidth + 4,
                        rowTop + itemHeight,
                        255 / 2 shl 24
                    )
                }
                entry.render(
                    matrixStack, i, rowTop, rowLeft, w, h, mouseX, mouseY,
                    isMouseOver(mouseX.toDouble(), mouseY.toDouble()) &&
                        Objects.equals(getEntryAtPosition(mouseX.toDouble(), mouseY.toDouble()), entry),
                    partialTicks
                )
            }
        }
    }

    override fun render(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderList(matrixStack, rowLeft, top + 4 - scrollAmount.toInt(), mouseX, mouseY, partialTicks)
    }

    fun selectStructure(entry: StructureSearchEntry?) {
        setSelected(entry)
        screen.selectStructure(entry)
    }

    fun selectStructure(structure: StructureFeature<*>) {
        selectStructure(map[structure.getIdentifier()])
    }

    fun refresh() {
        clearEntries()
        for (structure in screen.sortStructures()) {
            val entry = StructureSearchEntry(this, structure)
            addEntry(entry)
            map[structure.getIdentifier()!!] = entry
        }
        selectStructure(null)
        scrollAmount = 0.0
        changeFocus(false)
    }

    fun restoreScrollAmount() {
        if (selectedOrNull != null) {
            changeFocus(true)
            scrollAmount += itemHeight * 2
        }
    }
}
