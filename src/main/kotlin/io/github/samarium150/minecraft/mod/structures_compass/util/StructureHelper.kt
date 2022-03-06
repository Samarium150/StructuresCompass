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
package io.github.samarium150.minecraft.mod.structures_compass.util

import io.github.samarium150.minecraft.mod.structures_compass.config.FilterMode
import io.github.samarium150.minecraft.mod.structures_compass.config.StructuresCompassConfig
import io.github.samarium150.minecraft.mod.structures_compass.data.StructuresCompassData
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import net.minecraft.world.gen.feature.StructureFeature
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun Identifier.getStructureFeature(): StructureFeature<*>? {
    return Registry.STRUCTURE_FEATURE.get(this)
}

fun StructureFeature<*>.getIdentifier(): Identifier? {
    return Registry.STRUCTURE_FEATURE.getId(this)
}

fun StructureFeature<*>.getNamespace(): String? {
    return Registry.STRUCTURE_FEATURE.getId(this)?.namespace
}

fun StructureFeature<*>.getDimensions(world: ServerWorld): List<Identifier> {
    val dimensions = mutableListOf<Identifier>()
    world.server.worlds.forEach {
        if (it.canGenerate(this))
            dimensions.add(it.registryKey.value)
    }
    return dimensions
}

fun ServerWorld.canGenerate(structure: StructureFeature<*>): Boolean {
    val generator = chunkManager.chunkGenerator
    val config = generator.structuresConfig
    val registry = this.registryManager.get(Registry.BIOME_KEY)
    if (config.getForType(structure) != null && structure == StructureFeature.STRONGHOLD)
        return this.registryKey == World.OVERWORLD
    val biomes: Set<String> = generator.biomeSource.biomes.mapNotNull(registry::getId).map(Identifier::toString).toSet()
    for (biome in config.getConfiguredStructureFeature(structure).values()) {
        if (biomes.contains(biome.value.toString()))
            return true
    }
    return false
}

fun StructureFeature<*>.getDimensions(): List<Identifier> {
    return StructuresCompassData.structuresDimensionsMap[this] ?: listOf()
}

fun BlockPos.getDistanceVector(entity: Entity): Vec3d {
    val disX = (((this.x - entity.x) * 100).roundToInt() / 100).toDouble()
    val disY = if (this.y == 0) 0.0 else (((this.y - entity.y) * 100).roundToInt() / 100).toDouble()
    val disZ = (((this.z - entity.z) * 100).roundToInt() / 100).toDouble()
    return Vec3d(disX, disY, disZ)
}

fun Vec3d.getLength(): Double {
    return sqrt(this.x * this.x + this.y * this.y + this.z * this.z)
}

fun Identifier.isBanned(): Boolean {
    val flag = StructuresCompassConfig.configData.common.filterMode == FilterMode.WHITELIST
    val filters = StructuresCompassConfig.configData.common.filterList
    for (filter in filters) {
        val matching = this.toString().matches(filter.convertToRegex())
        if (flag && matching) return false
        else if (matching)
            return true
    }
    return flag
}
