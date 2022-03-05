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

data class ConfigComments(
    val _filterMode: String = "The mode of the filter, either blacklist or whitelist, default is blacklist",
    val _filter: String = "A list of structures that the compass will not search in blacklist mode " +
        "or will only search in whitelist mode, specified by resource location, supporting regex",
    val _maxDistance: String = "The pseudo maximum searching radius. " +
        "If the distance to the structure exceeds this value, HUD would display 'Not Found'",
    val _radius: String = "The real maximum searching radius used by the underlying method (no idea how it works.)" +
        "If you still couldn't find a structure with a big enough MaxSearchRadius, increase this one." +
        "If you think searching makes the server slow, decrease this one.",
    val _HudInfoLevel: String = "HUD information detail level. 0: Nothing." +
        "1+: Structure and Dimension name." +
        "2+: Distance to the structure." +
        "3: Position of the structure and distance in x/y/z axis.",
    val _HudPosition: String = "The side of the information HUD. Either LEFT or RIGHT.",
    val _displayWithChatOpen: String = "Displays the compass information HUD even while chat is open.",
    val _xOffset: String = "The X offset for information rendered on the HUD. (default:7)",
    val _yOffset: String = "The Y offset for information rendered on the HUD. (default:16)",
    val _overlayLineOffset: String = "The line offset for information rendered on the HUD. (default:1)",
    val _closedEnough: String = "The X/Y/Z-distance won't be shown " +
        "if the distance is smaller than the value. (default:0.3)"
)
