package com.github.samarium150.structurescompass;

import com.github.samarium150.structurescompass.client.HUDPosition;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;

public class StructuresCompassConfig {
    
    public static ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.ConfigValue<ArrayList<String>> blacklist;
    
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("configs for both server and client").push("General");
        blacklist = builder
                        .comment("A list of structures that the compass will not search, " +
                                     "specified by resource location. " +
                                     "Ex: [\"fortress\", \"quark:big_dungeon\"]")
                        .define("blacklist", new ArrayList<>());
        COMMON_CONFIG = builder.build();
    }
    
    public static ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec.EnumValue<HUDPosition> hudPosition;
    
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("configs only for client").push("Client");
        hudPosition = builder
                          .comment("The side of the information HUD. Ex: LEFT, RIGHT")
                          .defineEnum("HUDPosition", HUDPosition.LEFT);
        CLIENT_CONFIG = builder.build();
    }
}
