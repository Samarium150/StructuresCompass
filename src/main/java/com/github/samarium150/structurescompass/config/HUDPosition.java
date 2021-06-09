package com.github.samarium150.structurescompass.config;

/**
 * Enum for the configuration of HUD's position
 */
public enum HUDPosition {
    
    LEFT, RIGHT;
    
    public static HUDPosition fromString(String str) {
        return str != null && str.equals("RIGHT") ? RIGHT : LEFT;
    }
}
