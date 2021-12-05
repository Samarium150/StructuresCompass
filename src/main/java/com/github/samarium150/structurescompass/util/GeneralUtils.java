package com.github.samarium150.structurescompass.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
    
    /**
     * Split string into equal-length substrings
     * @param text target string
     * @param size size of substrings
     * @return list of substrings
     */
    @Nonnull
    public static List<String> splitEqually(@Nonnull String text, int size) {
        List<String> ret = new ArrayList<>((text.length() + size - 1) / size);
        for (int start = 0; start < text.length(); start += size)
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        return ret;
    }
    
    /**
     * Convert string to regex
     * @param glob input string
     * @return regex string
     */
    @Nonnull
    public static String convertToRegex(@Nonnull String glob) {
        StringBuilder regex = new StringBuilder("^");
        for (char i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);
            switch (c) {
                case '*':
                    regex.append(".*");
                    break;
                case '?':
                    regex.append(".");
                    break;
                case '.':
                    regex.append("\\.");
                    break;
                default:
                    regex.append(c);
            }
        }
        regex.append("$");
        return regex.toString();
    }
}
