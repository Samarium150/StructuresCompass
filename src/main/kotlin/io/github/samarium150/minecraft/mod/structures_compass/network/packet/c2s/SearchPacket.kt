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
package io.github.samarium150.minecraft.mod.structures_compass.network.packet.c2s

import io.github.samarium150.minecraft.mod.structures_compass.util.MOD_ID
import io.github.samarium150.minecraft.mod.structures_compass.util.search
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class SearchPacket(structureId: Identifier) : PacketByteBuf(Unpooled.buffer()) {

    companion object : PacketHandler {

        val ID = Identifier(MOD_ID, "search_packet")

        override fun receive(
            server: MinecraftServer,
            player: ServerPlayerEntity,
            handler: ServerPlayNetworkHandler,
            buf: PacketByteBuf,
            responseSender: PacketSender
        ) {
            val structureId = buf.readIdentifier()
            server.execute {
                player.mainHandStack.search(player, structureId)
            }
        }
    }

    init {
        writeIdentifier(structureId)
    }
}
