package com.github.samarium150.structurescompass.recipe;

import com.github.samarium150.structurescompass.config.StructuresCompassConfig;
import com.github.samarium150.structurescompass.util.GeneralUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

/**
 * Class for customizing recipe
 * <p>
 * Just modified the vanilla ShapedRecipe a little bit
 * @see ShapedRecipe
 */
public final class StructuresCompassItemRecipe implements ICraftingRecipe, IShapedRecipe<CraftingInventory> {
    
    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;
    
    private final int recipeWidth;
    private final int recipeHeight;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack recipeOutput;
    private final ResourceLocation id;
    private final String group;
    private final String condition;
    public static final Serializer serializer = new Serializer();
    
    public StructuresCompassItemRecipe(
        ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn,
        NonNullList<Ingredient> recipeItemsIn, String condition, ItemStack recipeOutputIn
    ) {
        this.id = idIn;
        this.group = groupIn;
        this.recipeWidth = recipeWidthIn;
        this.recipeHeight = recipeHeightIn;
        this.recipeItems = recipeItemsIn;
        this.condition = condition;
        this.recipeOutput = recipeOutputIn;
    }
    
    @Nonnull
    public ResourceLocation getId() {
        return this.id;
    }
    
    @Nonnull
    public IRecipeSerializer<?> getSerializer() {
        return serializer;
    }
    
    /**
     * Recipes with equal group are combined into one button in the recipe book
     */
    @Nonnull
    public String getGroup() {
        return this.group;
    }
    
    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    @Nonnull
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }
    
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }
    
    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height) {
        return width >= this.recipeWidth && height >= this.recipeHeight;
    }
    
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(@Nonnull CraftingInventory inventory, @Nonnull World world) {
        for(int i = 0; i <= inventory.getWidth() - this.recipeWidth; ++i) {
            for(int j = 0; j <= inventory.getHeight() - this.recipeHeight; ++j) {
                if (this.checkMatch(inventory, i, j, true)) {
                    return true;
                }
                
                if (this.checkMatch(inventory, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(@Nonnull CraftingInventory craftingInventory, int width, int height, boolean p_77573_4_) {
        for(int i = 0; i < craftingInventory.getWidth(); ++i) {
            for(int j = 0; j < craftingInventory.getHeight(); ++j) {
                int k = i - width;
                int l = j - height;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
                    if (p_77573_4_) {
                        ingredient = this.recipeItems.get(this.recipeWidth - k - 1 + l * this.recipeWidth);
                    } else {
                        ingredient = this.recipeItems.get(k + l * this.recipeWidth);
                    }
                }
                
                if (!ingredient.test(craftingInventory.getStackInSlot(i + j * craftingInventory.getWidth()))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Returns an Item that is the result of this recipe
     */
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
        return this.getRecipeOutput().copy();
    }
    
    public int getWidth() {
        return this.recipeWidth;
    }
    
    @Override
    public int getRecipeWidth() {
        return getWidth();
    }
    
    public int getHeight() {
        return this.recipeHeight;
    }
    
    @Override
    public int getRecipeHeight() {
        return getHeight();
    }
    
    @Nonnull
    private static NonNullList<Ingredient> deserializeIngredients(
        @Nonnull String[] pattern, @Nonnull Map<String, Ingredient> keys,
        int patternWidth, int patternHeight
    ) {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(keys.keySet());
        set.remove(" ");
        
        for(int i = 0; i < pattern.length; ++i)
            for(int j = 0; j < pattern[i].length(); ++j) {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = keys.get(s);
                if (ingredient == null)
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                set.remove(s);
                ingredients.set(j + patternWidth * i, ingredient);
            }
        
        if (!set.isEmpty())
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        else
            return ingredients;
        
    }
    
    @Nonnull
    @VisibleForTesting
    static String[] shrink(@Nonnull String... toShrink) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;
        
        for (int i1 = 0; i1 < toShrink.length; ++i1) {
            String s = toShrink[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }
                
                ++l;
            } else {
                l = 0;
            }
        }
        
        if (toShrink.length == l)
            return new String[0];
        else {
            String[] strings = new String[toShrink.length - l - k];
            for(int k1 = 0; k1 < strings.length; ++k1)
                strings[k1] = toShrink[k1 + k].substring(i, j + 1);
            return strings;
        }
    }
    
    private static int firstNonSpace(@Nonnull String str) {
        int i;
        for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i) { }
        return i;
    }
    
    private static int lastNonSpace(@Nonnull String str) {
        int i;
        for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) { }
        return i;
    }
    
    @Nonnull
    private static String[] patternFromJson(@Nonnull JsonArray jsonArr) {
        String[] str = new String[jsonArr.size()];
        if (str.length > MAX_HEIGHT)
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        else if (str.length == 0)
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        else {
            for(int i = 0; i < str.length; ++i) {
                String s = JSONUtils.getString(jsonArr.get(i), "pattern[" + i + "]");
                
                if (s.length() > MAX_WIDTH)
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
                if (i > 0 && str[0].length() != s.length())
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                
                str[i] = s;
            }
            return str;
        }
    }
    
    /**
     * Returns a key json object as a Java HashMap.
     */
    @Nonnull
    private static Map<String, Ingredient> deserializeKey(@Nonnull JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();
        
        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1)
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() +
                                                  "' is an invalid symbol (must be 1 character only).");
            
            if (" ".equals(entry.getKey()))
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            
            map.put(entry.getKey(), Ingredient.deserialize(entry.getValue()));
        }
        
        map.put(" ", Ingredient.EMPTY);
        return map;
    }
    
    public static class Serializer
        extends ForgeRegistryEntry<IRecipeSerializer<?>>
        implements IRecipeSerializer<StructuresCompassItemRecipe> {
        
        private static final ResourceLocation NAME = new ResourceLocation(GeneralUtils.MOD_ID, "recipe");
        
        Serializer() {
            this.setRegistryName(NAME);
        }
        
        @Nonnull
        public StructuresCompassItemRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            Map<String, Ingredient> map = StructuresCompassItemRecipe.deserializeKey(
                JSONUtils.getJsonObject(json, "key")
            );
            String[] patterns = StructuresCompassItemRecipe.shrink(
                StructuresCompassItemRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern"))
            );
            int width = patterns[0].length();
            int height = patterns.length;
            NonNullList<Ingredient> ingredients = StructuresCompassItemRecipe.deserializeIngredients(patterns, map, width, height);
            String condition = JSONUtils.getString(json, "condition", "normal");
            ItemStack itemstack = (condition.equalsIgnoreCase("easy")
                                       && StructuresCompassConfig.easyCrafting.get()) ||
                                      (condition.equalsIgnoreCase("normal") &&
                                           !StructuresCompassConfig.easyCrafting.get()) ?
                                      ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result")) :
                                      ItemStack.EMPTY;
            return new StructuresCompassItemRecipe(recipeId, group, width, height, ingredients, condition, itemstack);
        }
        
        public StructuresCompassItemRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            String s = buffer.readString(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
            
            for(int k = 0; k < ingredients.size(); ++k)
                ingredients.set(k, Ingredient.read(buffer));
            String condition = buffer.readString();
            ItemStack itemstack = (condition.equalsIgnoreCase("easy")
                                       && StructuresCompassConfig.easyCrafting.get()) ||
                                      (condition.equalsIgnoreCase("normal") &&
                                           !StructuresCompassConfig.easyCrafting.get()) ?
                                      buffer.readItemStack() : ItemStack.EMPTY;
            return new StructuresCompassItemRecipe(recipeId, s, width, height, ingredients, condition, itemstack);
        }
        
        public void write(@Nonnull PacketBuffer buffer, @Nonnull StructuresCompassItemRecipe recipe) {
            buffer.writeVarInt(recipe.recipeWidth);
            buffer.writeVarInt(recipe.recipeHeight);
            buffer.writeString(recipe.group);
            for(Ingredient ingredient : recipe.recipeItems)
                ingredient.write(buffer);
            buffer.writeString(recipe.condition);
            buffer.writeItemStack(recipe.recipeOutput);
        }
    }
}
