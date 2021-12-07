package com.github.samarium150.structurescompass.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;

/**
 * The configuration of the mod
 */
public final class StructuresCompassConfig {
    
    public enum Mode {
        blacklist,
        whitelist
    }
    
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.EnumValue<Mode> filterMode;
    public static final ForgeConfigSpec.ConfigValue<ArrayList<String>> filter;
    public static final ForgeConfigSpec.DoubleValue maxDistance;
    public static final ForgeConfigSpec.IntValue radius;
    public static final ForgeConfigSpec.IntValue HUD_Level;
    
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("configs for both server and clients").push("General");
        filterMode = builder
            .comment("\nThe mode of the filter, either blacklist or whitelist, default is blacklist")
            .defineEnum("filterMode", Mode.blacklist);
        
        filter = builder
            .comment("\nA list of structures that the compass will not search in blacklist mode " +
                         "or will only search in whitelist mode, " +
                         "specified by resource location, supporting regex.",
                "Ex: [\"minecraft:stronghold\", \"quark:big_dungeon\"]")
            .define("filter", new ArrayList<>());
        
        maxDistance = builder
            .comment("\nThe pseudo maximum searching radius.",
                "If the distance to the structure exceeds this value, HUD would display 'Not Found'")
            .defineInRange("MaxSearchRadius", 5000D, 20, 20000);
        
        radius = builder
            .comment("\nThe real maximum searching radius used by the underlying method (no idea how it works.)",
                "If you still couldn't find a structure with a big enough MaxSearchRadius, increase this one.",
                "If you think searching makes the server slow, decrease this one.")
            .defineInRange("RealRadius", 64, 1, 128);
        
        HUD_Level = builder
            .comment("\nHUD information detail level.",
                "0: Nothing.",
                "1+: Structure and Dimension name.",
                "2+: Distance to the structure.",
                "3: Position of the structure and distance in x/y/z axis.")
            .defineInRange("HUD_Level", 3, 0, 3);
        
        COMMON_CONFIG = builder.build();
        builder.pop();
    }
    
    public static final ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec.EnumValue<HUDPosition> hudPosition;
    public static final ForgeConfigSpec.BooleanValue displayWithChatOpen;
    public static final ForgeConfigSpec.IntValue overlayLineOffset;
    public static final ForgeConfigSpec.IntValue xOffset;
    public static final ForgeConfigSpec.IntValue yOffset;
    public static final ForgeConfigSpec.DoubleValue closeEnough;
    
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("configs only for clients").push("Client");
        hudPosition = builder
            .comment("\nThe side of the information HUD. Ex: LEFT, RIGHT")
            .defineEnum("HUDPosition", HUDPosition.LEFT);
        
        displayWithChatOpen = builder
            .comment("\nDisplays the compass information HUD even while chat is open.(default:true)")
            .define("DisplayWithChatOpen", true);
        
        xOffset = builder
            .comment("\nThe X offset for information rendered on the HUD.(default:7)")
            .defineInRange("xOffset", 7, 0 , 9600);

        yOffset = builder
            .comment("\nThe Y offset for information rendered on the HUD.(default:16)")
            .defineInRange("yOffset", 16, 0 , 5400);

        overlayLineOffset = builder
            .comment("\nThe line offset for information rendered on the HUD.(default:1)")
            .defineInRange("OverlayLineOffset", 1, 0 , 50);

        closeEnough = builder
            .comment("\nThe X/Y/Z-distance won't be shown if it is smaller than the value.(default:0.3)")
            .defineInRange("CloseEnough", 0.3, 0 , 50);
        
        CLIENT_CONFIG = builder.build();
        builder.pop();
    }
    
    private StructuresCompassConfig() { }
}
