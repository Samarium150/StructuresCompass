package com.github.samarium150.structurescompass.util.sort;

import com.github.samarium150.structurescompass.util.StructureUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Class for sorting structures by their dimensions
 */
@OnlyIn(Dist.CLIENT)
public final class DimensionCategory implements Category {

    @Override
    public int compare(Structure<?> s1, Structure<?> s2) {
        return StructureUtils.getDimensions(s1).compareTo(StructureUtils.getDimensions(s2));
    }

    @Nonnull
    @Override
    public Category next() {
        return new NameCategory();
    }

    @Nonnull
    @Override
    public String getLocalizedName() {
        return I18n.format("string.structurescompass.dimension");
    }
}
