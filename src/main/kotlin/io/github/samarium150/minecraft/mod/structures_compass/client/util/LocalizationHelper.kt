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
package io.github.samarium150.minecraft.mod.structures_compass.client.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.resource.language.I18n
import net.minecraft.util.Identifier
import java.util.*

@Environment(EnvType.CLIENT)
fun Identifier.getLocalizedStructureName(): String {
    return I18n.translate("structure.${this.toString().replace(':', '.').lowercase(Locale.ROOT)}")
}

@Environment(EnvType.CLIENT)
fun Identifier.getLocalizedDimensionName(): String {
    return I18n.translate("dimension.${this.toString().replace(':', '.').lowercase(Locale.ROOT)}")
}
