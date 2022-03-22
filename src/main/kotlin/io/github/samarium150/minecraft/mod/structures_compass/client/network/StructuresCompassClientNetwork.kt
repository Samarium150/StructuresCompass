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
package io.github.samarium150.minecraft.mod.structures_compass.client.network

import io.github.samarium150.minecraft.mod.structures_compass.network.packet.s2c.SyncPacket
import io.github.samarium150.minecraft.mod.structures_compass.util.MOD_ID
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import org.apache.logging.log4j.LogManager

@Environment(EnvType.CLIENT)
object StructuresCompassClientNetwork {

    private val logger = LogManager.getLogger("${MOD_ID}/client_network")

    fun init() {
        ClientPlayNetworking.registerGlobalReceiver(SyncPacket.ID) { client, handler, buf, responseSender ->
            logger.info("${SyncPacket.ID} received")
            SyncPacket.receive(client, handler, buf, responseSender)
        }
    }
}
