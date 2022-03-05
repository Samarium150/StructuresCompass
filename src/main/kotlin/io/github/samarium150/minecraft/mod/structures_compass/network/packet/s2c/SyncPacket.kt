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
package io.github.samarium150.minecraft.mod.structures_compass.network.packet.s2c

import io.github.samarium150.minecraft.mod.structures_compass.client.gui.StructuresCompassScreen
import io.github.samarium150.minecraft.mod.structures_compass.data.StructuresCompassData
import io.github.samarium150.minecraft.mod.structures_compass.util.MOD_ID
import io.github.samarium150.minecraft.mod.structures_compass.util.getIdentifier
import io.github.samarium150.minecraft.mod.structures_compass.util.getStructureFeature
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.world.gen.feature.StructureFeature

class SyncPacket(
    itemStack: ItemStack,
    allowedStructures: List<StructureFeature<*>>,
    structuresDimensionsMap: Map<StructureFeature<*>, List<Identifier>>
) : PacketByteBuf(Unpooled.buffer()) {

    companion object : ClientPlayNetworking.PlayChannelHandler {

        val ID = Identifier(MOD_ID, "sync_packet")

        override fun receive(
            client: MinecraftClient,
            handler: ClientPlayNetworkHandler,
            buf: PacketByteBuf,
            responseSender: PacketSender
        ) {
            val itemStack = buf.readItemStack()
            val size = buf.readInt()
            val allowedStructures = mutableListOf<StructureFeature<*>>()
            val structuresDimensionsMap = mutableMapOf<StructureFeature<*>, List<Identifier>>()
            for (i in 0 until size)
                buf.readIdentifier().getStructureFeature()?.let {
                    allowedStructures.add(it)
                    val dimensionSize = buf.readInt()
                    val dimensions = mutableListOf<Identifier>()
                    for (j in 0 until dimensionSize) dimensions.add(buf.readIdentifier())
                    structuresDimensionsMap[it] = dimensions
                }
            StructuresCompassData.update(allowedStructures, structuresDimensionsMap)
            client.execute {
                client.setScreen(StructuresCompassScreen(itemStack))
            }
        }
    }

    init {
        writeItemStack(itemStack)
        writeInt(allowedStructures.size)
        allowedStructures.forEach {
            writeIdentifier(it.getIdentifier())
            val dimensions = structuresDimensionsMap[it]
            if (dimensions != null) {
                writeInt(dimensions.size)
                dimensions.forEach { id ->
                    writeIdentifier(id)
                }
            }
        }
    }
}
