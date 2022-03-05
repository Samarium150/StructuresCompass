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

package io.github.samarium150.minecraft.mod.structures_compass.data

import io.github.samarium150.minecraft.mod.structures_compass.util.getDimensions
import io.github.samarium150.minecraft.mod.structures_compass.util.getIdentifier
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.StructureFeature

object StructuresCompassData {

    val allowedStructures: MutableList<StructureFeature<*>> = mutableListOf()
    val structuresDimensionsMap: MutableMap<StructureFeature<*>, List<Identifier>> = mutableMapOf()

    init {
        readAllowedStructures()
    }

    private fun readAllowedStructures() {
        Registry.STRUCTURE_FEATURE.forEach {
            if (it.getIdentifier() != null)
                allowedStructures.add(it)
        }
    }

    fun readStructureDimensionMap(serverWorld: ServerWorld) {
        allowedStructures.forEach {
            structuresDimensionsMap[it] = it.getDimensions(serverWorld)
        }
    }

    fun update(
        allowedStructures: List<StructureFeature<*>>,
        structuresDimensionsMap: Map<StructureFeature<*>, List<Identifier>>
    ) {
        this.allowedStructures.clear()
        this.allowedStructures.addAll(allowedStructures)
        this.structuresDimensionsMap.clear()
        this.structuresDimensionsMap.putAll(structuresDimensionsMap)
    }
}
