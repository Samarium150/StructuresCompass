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
package io.github.samarium150.minecraft.mod.structures_compass.init

import io.github.samarium150.minecraft.mod.structures_compass.server.command.GetCompass
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback

object CommandRegistry {
    fun init() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            run {
                GetCompass.register(dispatcher)
            }
        }
    }
}
