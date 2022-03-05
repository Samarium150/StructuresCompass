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
package io.github.samarium150.minecraft.mod.structures_compass.network

import io.github.samarium150.minecraft.mod.structures_compass.network.packet.c2s.RequestSyncPacket
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.c2s.SearchPacket
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.c2s.SetSkipPacket
import io.github.samarium150.minecraft.mod.structures_compass.util.MOD_ID
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager

object StructuresCompassServerNetwork {

    private val logger = LogManager.getLogger("${MOD_ID}/server_network")

    private val logTemplate: (Identifier, ServerPlayerEntity) -> String = { id, player ->
        "$id received from ${player.name.asString()}"
    }

    fun init() {
        ServerPlayNetworking.registerGlobalReceiver(
            SetSkipPacket.ID
        ) { server, player, handler, buf, responseSender ->
            logger.info(logTemplate(SetSkipPacket.ID, player))
            SetSkipPacket.receive(server, player, handler, buf, responseSender)
        }
        ServerPlayNetworking.registerGlobalReceiver(
            RequestSyncPacket.ID
        ) { server, player, handler, buf, responseSender ->
            logger.info(logTemplate(RequestSyncPacket.ID, player))
            RequestSyncPacket.receive(server, player, handler, buf, responseSender)
        }
        ServerPlayNetworking.registerGlobalReceiver(
            SearchPacket.ID
        ) { server, player, handler, buf, responseSender ->
            logger.info(logTemplate(SearchPacket.ID, player))
            SearchPacket.receive(server, player, handler, buf, responseSender)
        }
    }
}
