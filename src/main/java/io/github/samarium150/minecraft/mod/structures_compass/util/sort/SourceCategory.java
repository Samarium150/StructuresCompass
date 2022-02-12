package io.github.samarium150.minecraft.mod.structures_compass.util.sort;

import io.github.samarium150.minecraft.mod.structures_compass.util.GeneralUtils;
import io.github.samarium150.minecraft.mod.structures_compass.util.StructureUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Class for sorting structures by their source mod
 */
@OnlyIn(Dist.CLIENT)
public final class SourceCategory implements Category {
    
    @Override
    public int compare(StructureFeature<?> s1, StructureFeature<?> s2) {
        return StructureUtils.getStructureSource(s1).compareTo(StructureUtils.getStructureSource(s2));
    }
    
    @Nonnull
    @Override
    public Category next() {
        return new DimensionCategory();
    }
    
    @Nonnull
    @Override
    public String getLocalizedName() {
        return I18n.get(GeneralUtils.prefix + "source");
    }
}
