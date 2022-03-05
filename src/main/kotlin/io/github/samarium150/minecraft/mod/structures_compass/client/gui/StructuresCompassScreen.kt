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
package io.github.samarium150.minecraft.mod.structures_compass.client.gui

import io.github.samarium150.minecraft.mod.structures_compass.client.gui.widget.StructureSearchEntry
import io.github.samarium150.minecraft.mod.structures_compass.client.gui.widget.StructureSearchList
import io.github.samarium150.minecraft.mod.structures_compass.client.gui.widget.TransparentButton
import io.github.samarium150.minecraft.mod.structures_compass.client.gui.widget.TransparentTextField
import io.github.samarium150.minecraft.mod.structures_compass.client.util.getLocalizedStructureName
import io.github.samarium150.minecraft.mod.structures_compass.client.util.sort.Category
import io.github.samarium150.minecraft.mod.structures_compass.client.util.sort.NameCategory
import io.github.samarium150.minecraft.mod.structures_compass.data.StructuresCompassData
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.c2s.SearchPacket
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.c2s.SetSkipPacket
import io.github.samarium150.minecraft.mod.structures_compass.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.TranslatableText
import net.minecraft.world.gen.feature.StructureFeature
import java.util.*

@Environment(EnvType.CLIENT)
class StructuresCompassScreen(private val itemStack: ItemStack) : Screen(
    TranslatableText("${prefix}select_structure")
) {
    private val allowedStructures: List<StructureFeature<*>> = StructuresCompassData.allowedStructures
    private var structuresMatchingSearch: MutableList<StructureFeature<*>> = allowedStructures.toMutableList()
    private var sortingCategory: Category = NameCategory
    private lateinit var selectionList: StructureSearchList
    private var skip: Boolean = itemStack.getSkip()
    private lateinit var startSearchButton: ButtonWidget
    private lateinit var sortByButton: ButtonWidget
    private lateinit var skipExistingChunksButton: ButtonWidget
    private lateinit var searchTextField: TransparentTextField
    private var selected: StructureFeature<*>? = null

    private fun setup() {
        buttons.clear()
        startSearchButton = addButton(
            TransparentButton(
                10,
                40,
                110,
                20,
                TranslatableText("${prefix}start_searching")
            ) {
                selectionList.selected?.search()
            })
        sortByButton = addButton(
            TransparentButton(
                10,
                65,
                110,
                20,
                TranslatableText("${prefix}sort_by").append(": ${sortingCategory.localizedName}")
            ) {
                sortingCategory = sortingCategory.next()
                sortByButton.message = TranslatableText("${prefix}sort_by").append(": ${sortingCategory.localizedName}")
                selectionList.refresh()
            })
        skipExistingChunksButton = addButton(TransparentButton(
            10, 90, 110, 20,
            TranslatableText("${prefix}skip_existing_chunks").append(": $skip")
        ) {
            skip = !skip
            skipExistingChunksButton.message = TranslatableText("${prefix}skip_existing_chunks").append(": $skip")
        })
        addButton(TransparentButton(
            10, height - 30, 110, 20,
            TranslatableText("gui.cancel")
        ) {
            minecraftClient.currentScreen = null
        })
        searchTextField = addChild(TransparentTextField(
            textRenderer,
            130, 10, 140, 20,
            TranslatableText("${prefix}search")
        ))
        startSearchButton.active = false
    }

    fun selectStructure(entry: StructureSearchEntry?) {
        startSearchButton.active = entry != null
        if (entry != null) selected = entry.structure
    }

    fun sortStructures(): List<StructureFeature<*>> {
        Collections.sort(structuresMatchingSearch, NameCategory)
        Collections.sort(structuresMatchingSearch, sortingCategory)
        return structuresMatchingSearch
    }

    fun search(structure: StructureFeature<*>) {
        ClientPlayNetworking.send(SetSkipPacket.ID, SetSkipPacket(skip))
        ClientPlayNetworking.send(SearchPacket.ID, SearchPacket(structure.getIdentifier()!!))
        minecraftClient.currentScreen = null
    }

    private fun restoreSelected() {
        if (selected != null && structuresMatchingSearch.contains(selected)) selectionList.selectStructure(selected!!)
        selectionList.restoreScrollAmount()
    }

    private fun processSearchTerm() {
        structuresMatchingSearch = mutableListOf()
        for (structure in allowedStructures) {
            var temp = ""
            if (searchTextField.text.isNotEmpty() && searchTextField.text[0] == '#') temp =
                structure.getDimensions().toString()
            if ((searchTextField.text.isNotEmpty() &&  // source search
                    searchTextField.text[0] == '@' &&
                    structure.getNamespace().toString().lowercase(Locale.ROOT)
                        .contains(searchTextField.text.substring(1).lowercase(Locale.ROOT)) ||
                    (temp.isNotEmpty() && temp.lowercase(Locale.ROOT) // dim search
                        .contains(searchTextField.text.substring(1).lowercase(Locale.ROOT)))) ||
                (structure.getIdentifier()?.getLocalizedStructureName()?.lowercase(Locale.ROOT) // normal search
                    ?.contains(searchTextField.text.lowercase(Locale.getDefault())) == true)
            ) {
                structuresMatchingSearch.add(structure)
            }
        }
        selectionList.refresh()
        restoreSelected()
    }

    override fun init() {
        minecraftClient.keyboard.setRepeatEvents(true)
        setup()
        if (!this::selectionList.isInitialized)
            selectionList = StructureSearchList(
                this, width + 110, height, 40, height, 45
            )
        val structureId = itemStack.getStructure()
        if (structureId != null) {
            selectionList.selectStructure(structureId.getStructureFeature()!!)
            selectionList.changeFocus(true)
            selectionList.restoreScrollAmount()
        }
        addChild(selectionList)
    }

    override fun tick() {
        searchTextField.tick()
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        selectionList.render(matrices, mouseX, mouseY, delta)
        searchTextField.render(matrices, mouseX, mouseY, delta)
        drawCenteredText(matrices, textRenderer, title, 65, 15, 0xFFFFFF)
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        return selectionList.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
        val ret = super.charTyped(codePoint, modifiers)
        if (searchTextField.isFocused) {
            processSearchTerm()
            return true
        }
        return ret
    }

    override fun removed() {
        super.removed()
        minecraftClient.keyboard.setRepeatEvents(false)
    }
}
