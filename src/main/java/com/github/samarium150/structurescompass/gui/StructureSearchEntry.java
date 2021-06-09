package com.github.samarium150.structurescompass.gui;

import com.github.samarium150.structurescompass.util.StructureUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.list.ExtendedList.AbstractListEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Class of entries in the list for searching
 * <p>
 * This class is adapted from
 * <a href="https://github.com/MattCzyr/NaturesCompass" target="_blank">NaturesCompass</a>
 * which is under
 * <a href="https://creativecommons.org/licenses/by-nc-sa/4.0" target="_blank">
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License
 * </a>.
 */
@OnlyIn(Dist.CLIENT)
public final class StructureSearchEntry extends AbstractListEntry<StructureSearchEntry> {
    
    private final Minecraft minecraft;
    private final StructureSearchList list;
    private final Structure<?> structure;
    private final StructuresCompassScreen screen;
    private long lastTickTime;
    
    public StructureSearchEntry(@Nonnull StructureSearchList list, Structure<?> structure) {
        minecraft = Minecraft.getInstance();
        this.list = list;
        this.structure = structure;
        screen = list.getScreen();
    }
    
    public void search() {
        minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        screen.search(structure);
    }

    public Structure<?> getStructure() {
        return structure;
    }
    
//    @SuppressWarnings("deprecation")
//    @Override
//    public void render(
//        @Nonnull MatrixStack matrixStack,
//        int index, int top, int left, int width, int height, int mouseX, int mouseY,
//        boolean isMouseOver, float partialTicks
//    ) {
//        minecraft.fontRenderer.drawString(
//            StructureUtils.getLocalizedStructureName(structure),
//            left + 1, top + 1, 0xffffff
//        );
//        minecraft.fontRenderer.drawString(
//            I18n.format("string.structurescompass.source") + ": " + StructureUtils.getStructureSource(structure),
//            left + 1, top + minecraft.fontRenderer.FONT_HEIGHT + 3, 0x808080);
////        minecraft.fontRenderer.drawText(matrixStack,
////            new StringTextComponent(I18n.format("string.structurescompass.source") + ": "
////                                        + StructureUtils.getStructureSource(structure)),
////            left + 1, top + minecraft.fontRenderer.FONT_HEIGHT + 3, 0x808080
////        );
//        minecraft.fontRenderer.drawString(
//            I18n.format("string.structurescompass.dimension") + ": " + StructureUtils.getDimensions(structure),
//            left + 1, top + minecraft.fontRenderer.FONT_HEIGHT + 14, 0x808080);
////        minecraft.fontRenderer.drawText(
////            new StringTextComponent(I18n.format("string.structurescompass.dimension") + ": "
////                                        + StructureUtils.getDimensions(structure)),
////            left + 1, top + minecraft.fontRenderer.FONT_HEIGHT + 14, 0x808080
////        );
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            list.selectStructure(this);
            if (Util.milliTime() - lastTickTime < 250L) {
                search();
                return true;
            }
            lastTickTime = Util.milliTime();
        }
        return false;
    }
    
    @Override
    public void render(
        int index, int top, int left, int width, int height,
        int mouseX, int mouseY, boolean isMouseOver, float partialTicks
    ) {
        minecraft.fontRenderer.drawString(
            StructureUtils.getLocalizedStructureName(structure),
            left + 1, top + 1, 0xffffff
        );
        minecraft.fontRenderer.drawString(
            I18n.format("string.structurescompass.source") + ": " + StructureUtils.getStructureSource(structure),
            left + 1, top + minecraft.fontRenderer.FONT_HEIGHT + 3, 0x808080);
//        minecraft.fontRenderer.drawText(matrixStack,
//            new StringTextComponent(I18n.format("string.structurescompass.source") + ": "
//                                        + StructureUtils.getStructureSource(structure)),
//            left + 1, top + minecraft.fontRenderer.FONT_HEIGHT + 3, 0x808080
//        );
        minecraft.fontRenderer.drawString(
            I18n.format("string.structurescompass.dimension") + ": " + StructureUtils.getDimensions(structure),
            left + 1, top + minecraft.fontRenderer.FONT_HEIGHT + 14, 0x808080);
//        minecraft.fontRenderer.drawText(
//            new StringTextComponent(I18n.format("string.structurescompass.dimension") + ": "
//                                        + StructureUtils.getDimensions(structure)),
//            left + 1, top + minecraft.fontRenderer.FONT_HEIGHT + 14, 0x808080
//        );
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
