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
package io.github.samarium150.minecraft.mod.structures_compass.client.gui.hud

import io.github.samarium150.minecraft.mod.structures_compass.client.util.drawConfiguredStringOnHud
import io.github.samarium150.minecraft.mod.structures_compass.client.util.getLocalizedDimensionName
import io.github.samarium150.minecraft.mod.structures_compass.client.util.getLocalizedStructureName
import io.github.samarium150.minecraft.mod.structures_compass.config.ClientConfig
import io.github.samarium150.minecraft.mod.structures_compass.config.StructuresCompassConfig
import io.github.samarium150.minecraft.mod.structures_compass.init.ItemRegistry
import io.github.samarium150.minecraft.mod.structures_compass.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack

@Environment(EnvType.CLIENT)
object StructuresCompassHud {

    private val textRenderer: TextRenderer
        get() = minecraftClient.textRenderer

    private val config: ClientConfig
        get() = StructuresCompassConfig.configData.client

    fun render(matrixStack: MatrixStack) {
        if (minecraftClient.currentScreen == null ||
            (config.displayWithChatOpen && minecraftClient.currentScreen is ChatScreen)) {
            val player = minecraftClient.player
            if (player != null) {
                val itemStack = if (player.mainHandStack.item == ItemRegistry.STRUCTURES_COMPASS) {
                    player.mainHandStack
                } else if (player.offHandStack.item == ItemRegistry.STRUCTURES_COMPASS) {
                    player.offHandStack
                } else return
                val structureId = itemStack.getStructure()
                val dimension = itemStack.getDimension()
                val position = itemStack.getPos()
                var relLineOffset = 0
                if (structureId == null) return
                if (position == null || dimension == null) {
                    textRenderer.drawConfiguredStringOnHud(
                        matrixStack,
                        I18n.translate("${prefix}hud_not_found"),
                        5, 5, 0xAA2116, relLineOffset
                    )
                } else {
                    textRenderer.drawConfiguredStringOnHud(
                        matrixStack,
                        I18n.translate("${prefix}hud_structure"),
                        5, 5, 0xFFFFFF, relLineOffset
                    )
                    textRenderer.drawConfiguredStringOnHud(
                        matrixStack,
                        structureId.getLocalizedStructureName(),
                        5, 5, 0xAAAAAA, ++relLineOffset
                    )
                    relLineOffset++

                    textRenderer.drawConfiguredStringOnHud(
                        matrixStack,
                        I18n.translate("${prefix}hud_dim"),
                        5, 5, 0xFFFFFF, ++relLineOffset
                    )
                    textRenderer.drawConfiguredStringOnHud(
                        matrixStack,
                        dimension.getLocalizedDimensionName(),
                        5, 5, 0xAAAAAA, ++relLineOffset
                    )
                    relLineOffset++

                    if (config.HudInfoLevel == 3)
                        textRenderer.drawConfiguredStringOnHud(
                            matrixStack, I18n.translate("${prefix}hud_pos") +
                                " [${position.x}, ${if (position.y == 0) "X" else "${position.y}"}, ${position.z}]",
                            5, 5, 0x4AFF4A, ++relLineOffset
                        )

                    if (player.world.registryKey.value == dimension) {
                        val distanceVector = position.getDistanceVector(player)
                        val x = distanceVector.x
                        val y = distanceVector.y
                        val z = distanceVector.z
                        val distance = distanceVector.getLength()
                        if (config.HudInfoLevel == 3) {
                            if (x > 0.3)
                                textRenderer.drawConfiguredStringOnHud(
                                    matrixStack, "${I18n.translate("${prefix}hud_east")} $x",
                                    5, 5, 0xFFFFFF, ++relLineOffset
                                )
                            else if (x < -0.3)
                                textRenderer.drawConfiguredStringOnHud(
                                    matrixStack, "${I18n.translate("${prefix}hud_west")} ${-x}",
                                    5, 5, 0xFFFFFF, ++relLineOffset
                                )
                            if (z > 0.3)
                                textRenderer.drawConfiguredStringOnHud(
                                    matrixStack, "${I18n.translate("${prefix}hud_south")} $z",
                                    5, 5, 0xFFFFFF, ++relLineOffset
                                )
                            else if (z < -0.3)
                                textRenderer.drawConfiguredStringOnHud(
                                    matrixStack, "${I18n.translate("${prefix}hud_north")} ${-z}",
                                    5, 5, 0xFFFFFF, ++relLineOffset
                                )
                            if (y > 0.3)
                                textRenderer.drawConfiguredStringOnHud(
                                    matrixStack, "${I18n.translate("${prefix}hud_up")} $y",
                                    5, 5, 0xFFFFFF, ++relLineOffset
                                )
                            else if (y < -0.3)
                                textRenderer.drawConfiguredStringOnHud(
                                    matrixStack, "${I18n.translate("${prefix}hud_down")} ${-y}",
                                    5, 5, 0xFFFFFF, ++relLineOffset
                                )
                        }
                        if ((config.HudInfoLevel == 3 && relLineOffset > 6) ||
                            (config.HudInfoLevel >= 3 && distance > config.closedEnough)) {
                            relLineOffset++
                            textRenderer.drawConfiguredStringOnHud(
                                matrixStack, "${I18n.translate("${prefix}hud_distance")} %.3f".format(distance),
                                5, 5, 0xFFC20E, ++relLineOffset
                            )
                        }
                    } else {
                        relLineOffset++
                        textRenderer.drawConfiguredStringOnHud(
                            matrixStack, I18n.translate("${prefix}hud_wrong_dim"),
                            5, 5, 0xAA2116, ++relLineOffset
                        )
                    }
                }
            }
        }
    }
}
