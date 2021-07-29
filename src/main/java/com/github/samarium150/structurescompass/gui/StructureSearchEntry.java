package com.github.samarium150.structurescompass.gui;

import com.github.samarium150.structurescompass.util.StructureUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList.Entry;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
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
public final class StructureSearchEntry extends Entry<StructureSearchEntry> {
    
    private final Minecraft minecraft;
    private final StructureSearchList list;
    private final StructureFeature<?> structure;
    private final StructuresCompassScreen screen;
    private long lastTickTime;
    
    public StructureSearchEntry(@Nonnull StructureSearchList list, StructureFeature<?> structure) {
        minecraft = Minecraft.getInstance();
        this.list = list;
        this.structure = structure;
        screen = list.getScreen();
    }
    
    public void search() {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        screen.search(structure);
    }

    public StructureFeature<?> getStructure() {
        return structure;
    }
    
    @Override
    public void render(
        @Nonnull PoseStack matrixStack,
        int index, int top, int left, int width, int height, int mouseX, int mouseY,
        boolean isMouseOver, float partialTicks
    ) {
        minecraft.font.draw(matrixStack,
            StructureUtils.getLocalizedStructureName(structure),
            left + 1, top + 1, 0xffffff
        );
        minecraft.font.draw(matrixStack,
            new TextComponent(I18n.get("string.structurescompass.source") + ": "
                                        + StructureUtils.getStructureSource(structure)),
            left + 1, top + minecraft.font.lineHeight + 3, 0x808080
        );
        minecraft.font.draw(matrixStack,
            new TextComponent(I18n.get("string.structurescompass.dimension") + ": "
                                        + StructureUtils.getDimensions(structure)),
            left + 1, top + minecraft.font.lineHeight + 14, 0x808080
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            list.selectStructure(this);
            if (Util.getMillis() - lastTickTime < 250L) {
                search();
                return true;
            }
            lastTickTime = Util.getMillis();
        }
        return false;
    }
    
    @Nonnull
    @Override
    public Component getNarration() {
        return new TextComponent(I18n.get("string.structurescompass.source") + ": "
                                     + StructureUtils.getStructureSource(structure));
    }
}
