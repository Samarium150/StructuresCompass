package com.github.samarium150.structurescompass.client;

public enum HUDPosition {
    
    LEFT, RIGHT;
    
    public static HUDPosition fromString(String str) {
        return str != null && str.equals("RIGHT") ? RIGHT : LEFT;
    }
}
