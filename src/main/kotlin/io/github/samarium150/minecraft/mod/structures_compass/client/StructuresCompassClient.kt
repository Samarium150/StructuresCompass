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

package io.github.samarium150.minecraft.mod.structures_compass.client

import io.github.samarium150.minecraft.mod.structures_compass.client.init.HudRegistry
import io.github.samarium150.minecraft.mod.structures_compass.client.init.ItemPredicateRegistry
import io.github.samarium150.minecraft.mod.structures_compass.client.network.StructuresCompassClientNetwork
import net.fabricmc.api.ClientModInitializer

object StructuresCompassClient : ClientModInitializer {
    override fun onInitializeClient() {
        ItemPredicateRegistry.init()
        HudRegistry.init()
        StructuresCompassClientNetwork.init()
    }
}