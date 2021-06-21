package com.github.samarium150.structurescompass.init;

import com.github.samarium150.structurescompass.recipe.StructuresCompassItemRecipe;
import com.github.samarium150.structurescompass.recipe.StructuresCompassItemRecipeType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

/**
 * Registry of the recipe
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RecipeRegistry {
    
    public static final IRecipeType<StructuresCompassItemRecipe> recipeType = new StructuresCompassItemRecipeType();
    
    private RecipeRegistry() { }
    
    @SubscribeEvent
    public static void register(@Nonnull final Register<IRecipeSerializer<?>> event) {
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(recipeType.toString()), recipeType);
        event.getRegistry().register(StructuresCompassItemRecipe.serializer);
    }
}
