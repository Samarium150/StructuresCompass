/*
 * Copyright (c) 2020-2021 Samarium, SnightW
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, version 3 of the License
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.html>
 */
package io.github.samarium150.minecraft.mod.structures_compass;

import io.github.samarium150.minecraft.mod.structures_compass.config.StructuresCompassConfig;
import io.github.samarium150.minecraft.mod.structures_compass.util.GeneralUtils;
import io.github.samarium150.minecraft.mod.structures_compass.init.ItemRegistry;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main class of the mod
 * @see Mod
 */
@Mod(GeneralUtils.MOD_ID)
public final class StructuresCompass {
    
    /**
     * Initializer of the mod
     */
    public StructuresCompass() {
        ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructuresCompassConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, StructuresCompassConfig.CLIENT_CONFIG);
    }
}
