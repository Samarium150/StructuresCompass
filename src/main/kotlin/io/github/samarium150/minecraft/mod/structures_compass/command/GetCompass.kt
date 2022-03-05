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
package io.github.samarium150.minecraft.mod.structures_compass.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import io.github.samarium150.minecraft.mod.structures_compass.init.ItemRegistry
import io.github.samarium150.minecraft.mod.structures_compass.util.MOD_ID
import io.github.samarium150.minecraft.mod.structures_compass.util.setStructure
import net.minecraft.item.ItemStack
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object GetCompass {

    private fun execute(context: CommandContext<ServerCommandSource>, name: String): Int {
        context.source.player?.apply {
            giveItemStack(
                ItemStack(ItemRegistry.STRUCTURES_COMPASS).setStructure(Identifier(name))
            )
        }
        return Command.SINGLE_SUCCESS
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("sc").redirect(
                dispatcher.register(
                    CommandManager.literal(MOD_ID)
                        .requires { it.hasPermissionLevel(2) }
                        .apply {
                            Registry.STRUCTURE_FEATURE.forEach {
                                then(CommandManager.literal(it.name)
                                    .executes { context -> execute(context, it.name) }
                                )
                            }
                        }
                )
            )
        )
    }
}
