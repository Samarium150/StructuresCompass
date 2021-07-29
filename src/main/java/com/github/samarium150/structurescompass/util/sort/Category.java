package com.github.samarium150.structurescompass.util.sort;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * The interface for sorting structures in GUI
 */
@OnlyIn(Dist.CLIENT)
public interface Category extends Comparator<StructureFeature<?>> {
    
    /**
     * The comparison function for structures
     *
     * @param s1 first structure
     * @param s2 second structure
     * @return order of the two structures
     */
    @Override
    int compare(StructureFeature<?> s1, StructureFeature<?> s2);
    
    /**
     * @return the next Category after clicking
     */
    @Nonnull
    Category next();
    
    /**
     * @return the localized name of the Category
     */
    @Nonnull
    String getLocalizedName();
}
