package com.github.samarium150.structurescompass.util;

import com.github.samarium150.structurescompass.config.StructuresCompassConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
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
    
    public static List<Structure<?>> allowedStructures;
    
    public static HashMap<String, List<String>> structuresDimensionMap;
    
    StructureUtils() { }
    
    /**
     * Lookup ResourceLocation in ForgeRegistries
     * @param structure the given structure
     * @return the corresponding ResourceLocation
     */
    public static ResourceLocation getResourceForStructure(@Nonnull Structure<?> structure) {
        return ForgeRegistries.FEATURES.getKey(structure);
    }
    
    /**
     * Lookup Structures in ForgeRegistries
     * @param resource the given ResourceLocation
     * @return the corresponding structure
     */
    @Nullable
    public static Structure<?> getStructureForResource(ResourceLocation resource) {
        Feature<?> f = ForgeRegistries.FEATURES.getValue(resource);
        return (f instanceof Structure) ? (Structure<?>) f : null;
    }
    
    /**
     * Get all allowed structures
     * @return a list of allowed structures
     */
    @Nonnull
    public static List<Structure<?>> getAllowedStructures() {
        final List<Structure<?>> result = new ArrayList<>();
        for (Feature<?> feature : ForgeRegistries.FEATURES) {
            if (feature instanceof Structure) {
                ResourceLocation res = feature.getRegistryName();
                if (res == null || isStructureBanned(res.toString()))
                    continue;
                result.add((Structure<?>) feature);
            }
        }
        return result;
    }
    
    /**
     * Check the config whether the structure is banned
     * @param name the name of the structure
     * @return is banned or not
     */
    public static boolean isStructureBanned(String name) {
        return StructuresCompassConfig.blacklist.get().contains(name);
    }
    
    @Nonnull
    public static String cleanupResourceName(@Nonnull String resource) {
        return resource
                   .replace("nether_bridge", "fortress")
                   .replace("end_city", "endcity")
                   .replace("jungle_temple", "jungle_pyramid")
                   .replace("ocean_monument", "monument")
                   .replace("woodland_mansion", "mansion");
    }
    
    /**
     * Get the name of the structure
     * @param structure the given structure
     * @return the name of the structure
     */
    @Nonnull
    public static String getStructureName(@Nonnull Structure<?> structure) {
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
        String name = cleanupResourceName(resource.substring(split + 1));
        return I18n.format(String.format("structure.%s.%s", source, name));
    }
    
    /**
     * Get the localized name of the structure
     * @param structure the given structure
     * @return the localized name
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public static String getLocalizedStructureName(@Nonnull Structure<?> structure) {
        return getLocalizedStructureName(getStructureName(structure));
    }
    
    @Nonnull
    @SuppressWarnings("deprecation")
    public static String getDimensionName(@Nonnull DimensionType dimType) {
        ResourceLocation resource = Registry.DIMENSION_TYPE.getKey(dimType);
        return (resource == null) ? "" : resource.toString();
    }
    
    /**
     * Get a list of dimensions that will generate the structure
     * @param world player's server world
     * @param structure the given structure
     * @return a list of dimensions
     */
    @SuppressWarnings("deprecation")
    @Nonnull
    public static List<String> getDimensions(@Nonnull ServerWorld world, Structure<?> structure) {
        final List<String> dims = new ArrayList<>();
        MinecraftServer server = world.getServer();
        Registry<DimensionType> registry = Registry.DIMENSION_TYPE;
        registry.keySet().forEach(res -> {
            DimensionType dim = registry.getOrDefault(res);
            assert dim != null;
            ServerWorld w = server.getWorld(dim);
            if (w.getChunkProvider().getChunkGenerator().getBiomeProvider().hasStructure(structure))
                dims.add(getDimensionName(w.getDimension().getType()));
        });
        return dims;
    }
    
    /**
     * Get a string represented list of dimensions that will generate the structure
     * @param structure the given structure
     * @return a string represented list
     */
    @Nonnull
    public static String getDimensions(@Nonnull Structure<?> structure) {
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
        return I18n.format(String.format("dimension.%s.%s", source, name));
    }
    
    /**
     * Get the name of mod which registered the structure
     * @param structure the given structure
     * @return the name of the mod
     */
    public static String getStructureSource(@Nonnull Structure<?> structure) {
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
    public static Vec3d getDistance(@Nonnull BlockPos pos, @Nonnull Entity entity) {
        Vec3d entityPos = entity.getPositionVector();
        double disX = (double) Math.round((pos.getX() - entityPos.getX()) * 100) / 100;
        double disY = pos.getY() == 0 ? 0 : (double) Math.round((pos.getY() - entityPos.getY()) * 100) / 100;
        double disZ = (double) Math.round((pos.getZ() - entityPos.getZ()) * 100) / 100;
        return new Vec3d(disX, disY, disZ);
    }
}
