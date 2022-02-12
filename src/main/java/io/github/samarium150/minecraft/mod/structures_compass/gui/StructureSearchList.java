package io.github.samarium150.minecraft.mod.structures_compass.gui;

import io.github.samarium150.minecraft.mod.structures_compass.util.RenderUtils;
import io.github.samarium150.minecraft.mod.structures_compass.util.StructureUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

/**
 * Class of structures for searching
 * <p>
 * This class is adapted from
 * <a href="https://github.com/MattCzyr/NaturesCompass" target="_blank">NaturesCompass</a>
 * which is under
 * <a href="https://creativecommons.org/licenses/by-nc-sa/4.0" target="_blank">
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License
 * </a>.
 */
@OnlyIn(Dist.CLIENT)
public final class StructureSearchList extends ObjectSelectionList<StructureSearchEntry> {
    
    private final StructuresCompassScreen screen;
    private final HashMap<String, StructureSearchEntry> map = new HashMap<>();
    
    public StructureSearchList(StructuresCompassScreen screen, Minecraft minecraft,
                               int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(minecraft, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.screen = screen;
        refresh();
    }
    
    public StructuresCompassScreen getScreen() {
        return screen;
    }
    
    public void selectStructure(StructureSearchEntry entry) {
        setSelected(entry);
        screen.selectStructure(entry);
    }
    
    public void selectStructure(StructureFeature<?> structure) {
        selectStructure(map.get(StructureUtils.getStructureName(structure)));
    }
    
    public void refresh() {
        clearEntries();
        for (StructureFeature<?> structure : screen.sortStructures()) {
            StructureSearchEntry entry = new StructureSearchEntry(this, structure);
            addEntry(entry);
            map.put(StructureUtils.getStructureName(structure), entry);
        }
        selectStructure((StructureSearchEntry) null);
        setScrollAmount(0D);
        changeFocus(false);
    }
    
    public void restoreScrollAmount() {
        if (getSelected() != null) {
            changeFocus(true);
            setScrollAmount(this.getScrollAmount() + itemHeight * 2);
        }
    }
    
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 20;
    }
    
    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }
    
    @Override
    protected boolean isSelectedItem(int index) {
        return index >= 0 && index < children().size() && children().get(index).equals(getSelected());
    }
    
    @Override
    protected void renderList(@Nonnull PoseStack matrixStack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < getItemCount(); ++i) {
            int top = getRowTop(i);
            int bottom = top + itemHeight;
            if (bottom + height >= y0 && top <= y1) {
                StructureSearchEntry entry = getEntry(i);
                int h = itemHeight - 4;
                int w = getRowWidth();
                if (isSelectedItem(i)) {
                    final int insideLeft = x0 + width / 2 - getRowWidth() / 2 + 2;
                    RenderUtils.drawRect(insideLeft - 4, top - 4, insideLeft + getRowWidth() + 4, top + itemHeight, 255 / 2 << 24);
                }
                int left = getRowLeft();
                entry.render(
                    matrixStack, i, top, left, w, h, mouseX, mouseY,
                    isMouseOver(mouseX, mouseY) && Objects.equals(getEntryAtPosition(mouseX, mouseY), entry),
                    partialTicks
                );
            }
        }
    }
    
    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderList(matrixStack, getRowLeft(), y0 + 4 - (int) getScrollAmount(), mouseX, mouseY, partialTicks);
    }
}
