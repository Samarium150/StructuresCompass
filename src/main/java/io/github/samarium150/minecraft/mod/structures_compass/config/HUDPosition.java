package io.github.samarium150.minecraft.mod.structures_compass.config;

/**
 * Enum for the configuration of HUD's position
 */
public enum HUDPosition {
    
    LEFT, RIGHT;
    
    @SuppressWarnings("unused")
    public static HUDPosition fromString(String str) {
        return str != null && str.equals("RIGHT") ? RIGHT : LEFT;
    }
}
