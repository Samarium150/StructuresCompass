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
package io.github.samarium150.minecraft.mod.structures_compass.item

import io.github.samarium150.minecraft.mod.structures_compass.network.packet.c2s.RequestSyncPacket
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.c2s.SearchPacket
import io.github.samarium150.minecraft.mod.structures_compass.util.getStructure
import io.github.samarium150.minecraft.mod.structures_compass.util.getStructureFeature
import io.github.samarium150.minecraft.mod.structures_compass.util.prefix
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class StructuresCompassItem : Item(settings) {

    companion object {
        const val NAME = "structures_compass"
        private val settings: Settings = Settings().group(ItemGroup.TOOLS).maxCount(1)
        const val STRUCTURE_TAG = "Structure"
        const val DIM_TAG = "Dimension"
        const val POS_TAG = "Position"
        const val SKIP_TAG = "SkipExistingChunks"
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.mainHandStack
        if (world.isClient)
            if (user.isSneaking)
                ClientPlayNetworking.send(RequestSyncPacket.ID, RequestSyncPacket())
            else {
                val structureId = stack.getStructure()
                if (structureId == null) {
                    user.sendMessage(TranslatableText("${prefix}msg_no_target"), false)
                    return super.use(world, user, hand)
                }
                val structure = structureId.getStructureFeature()
                if (structure == null) {
                    user.sendMessage(TranslatableText("${prefix}msg_error_name"), false)
                    return super.use(world, user, hand)
                }
                ClientPlayNetworking.send(
                    SearchPacket.ID,
                    SearchPacket(structureId)
                )
            }
        return super.use(world, user, hand)
    }
}
