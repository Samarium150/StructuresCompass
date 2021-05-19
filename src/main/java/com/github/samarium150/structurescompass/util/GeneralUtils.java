package com.github.samarium150.structurescompass.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * General utilities for all other classes
 */
public abstract class GeneralUtils {
    
    public static final String MOD_ID = "structurescompass";
    
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    
    private GeneralUtils() { }
    
    /**
     * Swap arguments. <p>
     * Usage: z = swap(a, a=b, b=c, ... y=z);
     * @param args arguments
     * @param <T> generic types
     * @return the first argument
     */
    @SafeVarargs
    public static <T> T swap(@Nonnull T...args) {
        return args[0];
    }
}
