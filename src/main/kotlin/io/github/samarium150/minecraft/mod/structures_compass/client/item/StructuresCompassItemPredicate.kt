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
package io.github.samarium150.minecraft.mod.structures_compass.client.item

import io.github.samarium150.minecraft.mod.structures_compass.util.getDimension
import io.github.samarium150.minecraft.mod.structures_compass.util.getPos
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.item.ModelPredicateProvider
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

@Environment(EnvType.CLIENT)
object StructuresCompassItemPredicate : ModelPredicateProvider {

    data class AngleInterpolator(
        private var rotation: Double = 0.0,
        private var speed: Double = 0.0,
        private var lastUpdateTick: Long = 0
    ) {
        private fun shouldUpdate(tick: Long): Boolean {
            return lastUpdateTick != tick
        }

        fun update(world: ClientWorld?, amount: Double): Double {
            if (world != null && shouldUpdate(world.time)) {
                lastUpdateTick = world.time
                var d0 = amount - rotation
                d0 = MathHelper.floorMod(d0 + 0.5, 1.0) - 0.5
                speed += d0 * 0.1
                speed *= 0.8
                rotation = MathHelper.floorMod(rotation + speed, 1.0)
            }
            return rotation
        }
    }

    private val r1 = AngleInterpolator()
    private val r2 = AngleInterpolator()

    private fun getFrameRotation(frame: ItemFrameEntity): Double {
        val facing = frame.horizontalFacing
        val i = if (facing.axis.isVertical) 90 * facing.direction.offset() else 0
        return MathHelper.wrapDegrees(180 + facing.horizontal * 90 + frame.rotation * 45 + i).toDouble()
    }

    private fun closedEnough(entity: Entity, pos: BlockPos): Boolean {
        return entity.pos.squaredDistanceTo(pos.x + 0.5, entity.pos.y, pos.z + 0.5) < 1.0E-5F
    }

    private fun getAngle(vector: Vec3d, entity: Entity): Double {
        return MathHelper.atan2(vector.z - entity.pos.z, vector.x - entity.pos.x)
    }

    override fun call(stack: ItemStack?, clientWorld: ClientWorld?, livingEntity: LivingEntity?): Float {
        val entity: Entity = (livingEntity ?: stack?.holder) ?: return 0.0f
        val world: ClientWorld = clientWorld ?: entity.world as ClientWorld
        val pos = stack?.getPos()
        if (pos != null && world.registryKey.value == stack.getDimension() && !closedEnough(entity, pos)) {
            val flag = entity is PlayerEntity && entity.isMainPlayer
            var d1: Double = if (flag) {
                entity.yaw.toDouble()
            } else if (entity is ItemFrameEntity) {
                getFrameRotation(entity)
            } else if (entity is ItemEntity) {
                (180.0f - entity.method_27314(0.5f) / 6.2831855f * 360.0f).toDouble()
            } else livingEntity?.bodyYaw?.toDouble() ?: 0.0
            d1 = MathHelper.floorMod(d1 / 360.0, 1.0)
            val d2 = getAngle(Vec3d.ofCenter(pos), entity) / (2 * Math.PI)
            val d3 = if (flag) d2 + r1.update(world, 0.5 - (d1 - 0.25)) else 0.5 - (d1 - 0.25 - d2)
            return MathHelper.floorMod(d3, 1.0).toFloat()
        }
        return MathHelper.floorMod((r2.update(world, Math.random()) + stack.hashCode() / 2.14748365E9), 1.0).toFloat()
    }
}
