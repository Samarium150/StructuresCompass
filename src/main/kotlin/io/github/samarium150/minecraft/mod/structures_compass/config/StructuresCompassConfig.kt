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
package io.github.samarium150.minecraft.mod.structures_compass.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import io.github.samarium150.minecraft.mod.structures_compass.data.StructuresCompassData
import io.github.samarium150.minecraft.mod.structures_compass.util.MOD_ID
import net.fabricmc.loader.api.FabricLoader
import java.io.IOException
import java.nio.file.Files

object StructuresCompassConfig {

    private val configFilePath
        get() = FabricLoader.getInstance().configDir.resolve("$MOD_ID.json")

    private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    data class ConfigData(
        val comments: ConfigComments = ConfigComments(),
        val common: CommonConfig,
        val client: ClientConfig
    )

    var configData = ConfigData(common = CommonConfig(), client = ClientConfig())

    @Throws(SecurityException::class, IOException::class, JsonIOException::class)
    fun load() {
        if (Files.exists(configFilePath))
            Files.newBufferedReader(configFilePath).use { reader ->
                configData = gson.fromJson(reader, ConfigData::class.java)
            }
        save()
        StructuresCompassData.init()
    }

    @Throws(JsonIOException::class, IOException::class)
    private fun save() {
        Files.newBufferedWriter(configFilePath).use { writer ->
            gson.toJson(configData, writer)
        }
    }
}
