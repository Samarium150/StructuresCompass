package io.github.samarium150.minecraft.mod.structures_compass.util;

import io.github.samarium150.minecraft.mod.structures_compass.config.StructuresCompassConfig;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Utilities related to structures
 */
public abstract class StructureUtils {
    
    public static List<StructureFeature<?>> allowedStructures;
    
    public static HashMap<String, List<String>> structuresDimensionMap;
    
    StructureUtils() { }
    
    /**
     * Lookup ResourceLocation in ForgeRegistries
     * @param structure the given structure
     * @return the corresponding ResourceLocation
     */
    public static ResourceLocation getResourceForStructure(@Nonnull StructureFeature<?> structure) {
        return ForgeRegistries.STRUCTURE_FEATURES.getKey(structure);
    }
    
    /**
     * Lookup Structures in ForgeRegistries
     * @param resource the given ResourceLocation
     * @return the corresponding structure
     */
    @Nullable
    public static StructureFeature<?> getStructureForResource(ResourceLocation resource) {
        return ForgeRegistries.STRUCTURE_FEATURES.getValue(resource);
    }
    
    /**
     * Get all allowed structures
     * @return a list of allowed structures
     */
    @Nonnull
    public static List<StructureFeature<?>> getAllowedStructures() {
        final List<StructureFeature<?>> result = new ArrayList<>();
        for (StructureFeature<?> structureFeature : ForgeRegistries.STRUCTURE_FEATURES) {
            ResourceLocation res = structureFeature.getRegistryName();
            if (res == null || isStructureBanned(res.toString()))
                continue;
            result.add(structureFeature);
        }
        return result;
    }
    
    /**
     * Check the config whether the structure is banned
     * @param name the name of the structure
     * @return is banned or not
     */
    public static boolean isStructureBanned(String name) {
        boolean flag = StructuresCompassConfig.filterMode.get() == StructuresCompassConfig.Mode.whitelist;
        ArrayList<String> filters = StructuresCompassConfig.filter.get();
        for (String filter : filters) {
            String rx = GeneralUtils.convertToRegex(filter);
            boolean matching = name.matches(rx);
            if (flag && matching)
                return false;
            else if (matching)
                return true;
        }
        return flag;
    }
    
    /**
     * Get the name of the structure
     * @param structure the given structure
     * @return the name of the structure
     */
    @Nonnull
    public static String getStructureName(@Nonnull StructureFeature<?> structure) {
        ResourceLocation registry = structure.getRegistryName();
        return (registry == null) ? "" : registry.toString();
    }
    
    /**
     * Get the localized name of the structure
     * @param resource the string representation of the resource location
     * @return the localized name
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public static String getLocalizedStructureName(@Nonnull String resource) {
        if (resource.equals("")) return "";
        int split = resource.indexOf(":");
        if (split == -1) return resource;
        String source = resource.substring(0, split);
        String name = resource.substring(split + 1);
        return I18n.get(String.format("structure.%s.%s", source, name));
    }
    
    /**
     * Get the localized name of the structure
     * @param structure the given structure
     * @return the localized name
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public static String getLocalizedStructureName(@Nonnull StructureFeature<?> structure) {
        return getLocalizedStructureName(getStructureName(structure));
    }
    
    /**
     * Get a list of dimensions that will generate the structure
     * @param world player's server world
     * @param structure the given structure
     * @return a list of dimensions
     */
    @Nonnull
    public static List<String> getDimensions(@Nonnull ServerLevel world, StructureFeature<?> structure) {
//        final List<String> dims = new ArrayList<>();
//        MinecraftServer server = world.getServer();
//        server.getAllLevels().forEach(w->{
//            // canGenerateStructure method doesn't exist in 1.18
//            if (w.getChunkSource().getGenerator().getBiomeSource().canGenerateStructure(structure))
//                dims.add(w.dimension().location().toString());
//        });
        return new ArrayList<>();
    }
    
    /**
     * Get a string represented list of dimensions that will generate the structure
     * @param structure the given structure
     * @return a string represented list
     */
    @Nonnull
    public static String getDimensions(@Nonnull StructureFeature<?> structure) {
        List<String> dims = StructureUtils.structuresDimensionMap.getOrDefault(
            StructureUtils.getResourceForStructure(structure).toString(),
            new ArrayList<>()
        );
        return (dims == null) ? "" : dims.toString();
    }
    
    /**
     * Get the localized name of the dimension
     * @param resource the string representation of the resource location
     * @return the localized name
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public static String getLocalizedDimensionName(@Nonnull String resource) {
        int split = resource.indexOf(":");
        if (split == -1) return resource;
        String source = resource.substring(0, split);
        String name = resource.substring(split + 1);
        return I18n.get(String.format("dimension.%s.%s", source, name));
    }
    
    /**
     * Get the name of mod which registered the structure
     * @param structure the given structure
     * @return the name of the mod
     */
    public static String getStructureSource(@Nonnull StructureFeature<?> structure) {
        if (getResourceForStructure(structure) == null)
            return "";
        String registry = getResourceForStructure(structure).toString();
        String name = registry.substring(0, registry.indexOf(":"));
        if (name.equals("minecraft"))
            return "Minecraft";
        Optional<? extends ModContainer> source = ModList.get().getModContainerById(name);
        if (source.isPresent())
            return source.get().getModInfo().getDisplayName();
        return name;
    }
    
    /**
     * Get the distance vector of the block position and the entity
     * @param pos the given block position
     * @param entity the given entity
     * @return the distance vector
     */
    @Nonnull
    public static Vec3 getDistance(@Nonnull BlockPos pos, @Nonnull Entity entity) {
        double disX = (double) Math.round((pos.getX() - entity.getX()) * 100) / 100;
        double disY = pos.getY() == 0 ? 0 : (double) Math.round((pos.getY() - entity.getY()) * 100) / 100;
        double disZ = (double) Math.round((pos.getZ() - entity.getZ()) * 100) / 100;
        return new Vec3(disX, disY, disZ);
    }
}
