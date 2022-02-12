package io.github.samarium150.minecraft.mod.structures_compass.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.samarium150.minecraft.mod.structures_compass.item.StructuresCompassItem;
import io.github.samarium150.minecraft.mod.structures_compass.network.StructuresCompassNetwork;
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.CompassSearchPacket;
import io.github.samarium150.minecraft.mod.structures_compass.network.packet.CompassSkipExistingChunksPacket;
import io.github.samarium150.minecraft.mod.structures_compass.util.GeneralUtils;
import io.github.samarium150.minecraft.mod.structures_compass.util.StructureUtils;
import io.github.samarium150.minecraft.mod.structures_compass.util.sort.Category;
import io.github.samarium150.minecraft.mod.structures_compass.util.sort.NameCategory;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Class of the screen of the GUI
 * <p>
 * This class is adapted from
 * <a href="https://github.com/MattCzyr/NaturesCompass" target="_blank">NaturesCompass</a>
 * which is under
 * <a href="https://creativecommons.org/licenses/by-nc-sa/4.0" target="_blank">
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License
 * </a>.
 */
@OnlyIn(Dist.CLIENT)
public final class StructuresCompassScreen extends Screen {
    
    private final List<StructureFeature<?>> allowedStructures = StructureUtils.allowedStructures;
    private final ItemStack stack;
    private List<StructureFeature<?>> structuresMatchingSearch;
    private StructureSearchList selectionList;
    private Category category;
    private Button startSearchButton;
    private Button sortByButton;
    private Button skipExistingChunksButton;
    private EditBox searchTextField;
    private boolean skip;
    private StructureFeature<?> selected;
    
    public StructuresCompassScreen(@Nonnull ItemStack stack) {
        super(new TranslatableComponent(GeneralUtils.prefix + "select_structure"));
        this.stack = stack;
        structuresMatchingSearch = new ArrayList<>(allowedStructures);
        category = new NameCategory();
        skip = StructuresCompassItem.isSkip(stack);
    }
    
    private void setup() {
        renderables.clear();
        startSearchButton = addRenderableWidget(new TransparentButton(
            10, 40, 110, 20,
            new TranslatableComponent(GeneralUtils.prefix + "start_searching"),
            (onPress) -> {
                StructureSearchEntry entry = selectionList.getSelected();
                if (entry != null)
                    entry.search();
            }
        ));
        sortByButton = addRenderableWidget(new TransparentButton(
            10, 65, 110, 20,
            new TextComponent(
                I18n.get(GeneralUtils.prefix + "sort_by") + ": " + category.getLocalizedName()
            ),
            (onPress) -> {
                category = category.next();
                sortByButton.setMessage(new TextComponent(
                    I18n.get(GeneralUtils.prefix + "sort_by") + ": " + category.getLocalizedName())
                );
                selectionList.refresh();
                restoreSelected();
            }
        ));
        skipExistingChunksButton = addRenderableWidget(new TransparentButton(
            10, 90, 110, 20,
            new TextComponent(
                I18n.get(GeneralUtils.prefix + "skip_existing_chunks") + ": " + skip
            ),
            (onPress) -> {
                skip = !skip;
                skipExistingChunksButton.setMessage(new TextComponent(
                    I18n.get(GeneralUtils.prefix + "skip_existing_chunks") + ": " + skip)
                );
            }
        ));
        addRenderableWidget(new TransparentButton(
            10, height - 30, 110, 20,
            new TranslatableComponent("gui.cancel"),
            (onPress) -> {
                assert minecraft != null;
                minecraft.setScreen(null);
            }
        ));
        searchTextField = addWidget(new TransparentTextField(
            font, 130, 10, 140, 20,
            new TranslatableComponent(GeneralUtils.prefix + "search")
        ));
    }
    
    public void selectStructure(StructureSearchEntry entry) {
        startSearchButton.active = entry != null;
        if (entry != null) selected = entry.getStructure();
    }
    
    public List<StructureFeature<?>> sortStructures() {
        final List<StructureFeature<?>> structures = structuresMatchingSearch;
        structures.sort(new NameCategory());
        structures.sort(category);
        return structuresMatchingSearch;
    }
    
    public void search(@Nonnull StructureFeature<?> structure) {
        assert minecraft != null;
        StructuresCompassNetwork.channel.sendToServer(new CompassSkipExistingChunksPacket(skip));
        StructuresCompassNetwork.channel.sendToServer(new CompassSearchPacket(structure.getRegistryName()));
        minecraft.setScreen(null);
    }
    
    public void processSearchTerm() {
        structuresMatchingSearch = new ArrayList<>();
        for (StructureFeature<?> structure : allowedStructures) {
            String temp = "";
            if (!searchTextField.getValue().isEmpty() && searchTextField.getValue().charAt(0) == '#')
                temp = StructureUtils.getDimensions(structure);
            if ((!searchTextField.getValue().isEmpty() &&
                    // source search
                    (searchTextField.getValue().charAt(0) == '@' &&
                        StructureUtils.getStructureSource(structure).toLowerCase()
                            .contains(searchTextField.getValue().substring(1).toLowerCase())) ||
                    // dim search
                    (!temp.isEmpty() && temp.toLowerCase()
                            .contains(searchTextField.getValue().substring(1).toLowerCase()))) ||
                // normal search
                (StructureUtils.getLocalizedStructureName(structure).toLowerCase()
                    .contains(searchTextField.getValue().toLowerCase()))) {
                structuresMatchingSearch.add(structure);
            }
        }
        selectionList.refresh();
        restoreSelected();
    }

    public void restoreSelected() {
        if (selected != null && structuresMatchingSearch.contains(selected))
            selectionList.selectStructure(selected);
        selectionList.restoreScrollAmount();
    }
    
    @Override
    protected void init() {
        assert minecraft != null;
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        setup();
        if (selectionList == null)
            selectionList = new StructureSearchList(this, minecraft,
                width + 110, height, 40, height, 45
            );
        String selected = StructuresCompassItem.getStructureName(stack);
        if (selected != null) {
            selectionList.selectStructure(StructureUtils.getStructureForResource(new ResourceLocation(selected)));
            selectionList.changeFocus(true);
            selectionList.restoreScrollAmount();
        }
        addWidget(selectionList);
    }
    
    @Override
    public void tick() {
        searchTextField.tick();
    }
    
    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        selectionList.render(matrixStack, mouseX, mouseY, partialTicks);
        searchTextField.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, font, I18n.get(GeneralUtils.prefix + "select_structure"), 65, 15, 0xffffff);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean ret = super.keyPressed(keyCode, scanCode, modifiers);
        if (searchTextField.isFocused()) {
            processSearchTerm();
            return true;
        }
        return ret;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean ret = super.charTyped(codePoint, modifiers);
        if (searchTextField.isFocused()) {
            processSearchTerm();
            return true;
        }
        return ret;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return selectionList.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void removed() {
        super.removed();
        assert minecraft != null;
        minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
}
