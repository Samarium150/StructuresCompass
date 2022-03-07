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

import io.github.samarium150.minecraft.mod.structures_compass.config.StructuresCompassConfig
import io.github.samarium150.minecraft.mod.structures_compass.item.StructuresCompassItem
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryEntryList
import net.minecraft.util.registry.RegistryKey
import org.apache.logging.log4j.LogManager
import kotlin.math.roundToInt

private val logger = LogManager.getLogger("${MOD_ID}/ISH")

fun ItemStack.setStructure(structure: Identifier): ItemStack {
    this.orCreateNbt.putString(StructuresCompassItem.STRUCTURE_TAG, structure.toString())
    return this
}

fun ItemStack.getStructure(): Identifier? {
    val name = this.orCreateNbt.getString(StructuresCompassItem.STRUCTURE_TAG)
    return if (name.isNotEmpty()) Identifier(name) else null
}

fun ItemStack.setDimension(dimension: Identifier): ItemStack {
    this.orCreateNbt.putString(StructuresCompassItem.DIM_TAG, dimension.toString())
    return this
}

fun ItemStack.getDimension(): Identifier? {
    val name = this.orCreateNbt.getString(StructuresCompassItem.DIM_TAG)
    return if (name.isNotEmpty()) Identifier(name) else null
}

fun ItemStack.setPos(pos: BlockPos): ItemStack {
    this.orCreateNbt.putLong(StructuresCompassItem.POS_TAG, pos.asLong())
    return this
}

fun ItemStack.getPos(): BlockPos? {
    val tag = this.orCreateNbt
    return if (tag.contains(StructuresCompassItem.POS_TAG))
        BlockPos.fromLong(tag.getLong(StructuresCompassItem.POS_TAG))
    else null
}

fun ItemStack.setSkip(skip: Boolean): ItemStack {
    this.orCreateNbt.putBoolean(StructuresCompassItem.SKIP_TAG, skip)
    return this
}

fun ItemStack.getSkip(): Boolean {
    return this.orCreateNbt.getBoolean(StructuresCompassItem.SKIP_TAG)
}

fun ItemStack.removeTag(tag: String): ItemStack {
    this.nbt?.remove(tag)
    return this
}

fun ItemStack.search(player: ServerPlayerEntity, structureId: Identifier) {
    val structure = structureId.getStructureFeature()
    val radius = StructuresCompassConfig.configData.common.radius
    if (structure != null) {
        val world = player.world as ServerWorld
        setStructure(structureId)
        player.sendMessage(TranslatableText("${prefix}msg_searching"), false)
        val registryEntryList = world.registryManager
            .get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY)
            .getEntry(RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, structureId))
            .map {
                RegistryEntryList.of(it)
            }.get()
        val result = world.chunkManager.chunkGenerator.locateStructure(
            world, registryEntryList, player.blockPos, radius, getSkip()
        )
        player.sendMessage(TranslatableText("${prefix}msg_done"), false)
        if (result == null) {
            removeTag(StructuresCompassItem.DIM_TAG)
            removeTag(StructuresCompassItem.POS_TAG)
            logger.info("$structureId not found")
        } else {
            val pos = result.first
            val vec = pos.getDistanceVector(player)
            val distance = ((vec.length() * 100).roundToInt() / 100).toDouble()
            if (distance > 10000) {
                removeTag(StructuresCompassItem.DIM_TAG)
                removeTag(StructuresCompassItem.POS_TAG)
                logger.info("$structureId is too far away")
            } else {
                val dimension = world.registryKey.value
                setDimension(dimension)
                setPos(pos)
                logger.info("Found $structureId at $pos in $dimension")
            }
        }
    }
}
