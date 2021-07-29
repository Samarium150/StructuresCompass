package com.github.samarium150.structurescompass.util.sort;

import com.github.samarium150.structurescompass.util.StructureUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Class for sorting structures by their localized name
 */
@OnlyIn(Dist.CLIENT)
public final class NameCategory implements Category {
    
    @Override
    public int compare(StructureFeature<?> s1, StructureFeature<?> s2) {
        return StructureUtils.getLocalizedStructureName(s1).compareTo(StructureUtils.getLocalizedStructureName(s2));
    }
    
    @Nonnull
    @Override
    public Category next() {
        return new SourceCategory();
    }
    
    @Nonnull
    @Override
    public String getLocalizedName() {
        return I18n.get("string.structurescompass.name");
    }
}
