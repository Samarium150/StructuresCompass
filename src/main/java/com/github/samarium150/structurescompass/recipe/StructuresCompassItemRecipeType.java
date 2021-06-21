package com.github.samarium150.structurescompass.recipe;

import com.github.samarium150.structurescompass.util.GeneralUtils;
import net.minecraft.item.crafting.IRecipeType;

/**
 * Class for registering the recipe type
 */
public final class StructuresCompassItemRecipeType implements IRecipeType<StructuresCompassItemRecipe> {
    
    @Override
    public String toString() {
        return GeneralUtils.MOD_ID + ":recipe";
    }
}
