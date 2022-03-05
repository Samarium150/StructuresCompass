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

import com.mojang.blaze3d.systems.RenderSystem
import io.github.samarium150.minecraft.mod.structures_compass.client.util.getLocalizedDimensionName
import io.github.samarium150.minecraft.mod.structures_compass.client.util.getLocalizedStructureName
import io.github.samarium150.minecraft.mod.structures_compass.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget.Entry
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.world.gen.feature.StructureFeature

@Environment(EnvType.CLIENT)
class StructureSearchEntry(
    private val searchList: StructureSearchList,
    val structure: StructureFeature<*>
) : Entry<StructureSearchEntry>() {

    private val client = minecraftClient
    private val screen = searchList.screen
    private var lastClickTime = 0L

    fun search() {
        client.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
        screen.search(structure)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (button == 0) {
            searchList.selectStructure(this)
            return if (Util.getMeasuringTimeMs() - lastClickTime < 250L) {
                search()
                true
            } else {
                lastClickTime = Util.getMeasuringTimeMs()
                false
            }
        } else false
    }

    override fun render(
        matrices: MatrixStack?,
        index: Int,
        y: Int,
        x: Int,
        entryWidth: Int,
        entryHeight: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        tickDelta: Float
    ) {
        client.textRenderer.draw(
            matrices,
            LiteralText(structure.getIdentifier()?.getLocalizedStructureName() ?: ""),
            (x + 1).toFloat(),
            (y + 1).toFloat(),
            0xffffff
        )
        client.textRenderer.draw(
            matrices,
            TranslatableText("${prefix}source").append(": ${structure.getNamespace()}"),
            (x + 1).toFloat(),
            (y + 3 + client.textRenderer.fontHeight).toFloat(),
            0x808080
        )
        client.textRenderer.draw(
            matrices,
            TranslatableText("${prefix}dimension")
                .append(": ${structure.getDimensions().map(Identifier::getLocalizedDimensionName)}"),
            (x + 1).toFloat(),
            (y + 14 + client.textRenderer.fontHeight).toFloat(),
            0x808080
        )
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

    override fun getNarration(): Text {
        return LiteralText(structure.getIdentifier()!!.getLocalizedStructureName())
    }
}
