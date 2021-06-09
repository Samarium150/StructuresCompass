package com.github.samarium150.structurescompass.gui;

import com.github.samarium150.structurescompass.item.StructuresCompassItem;
import com.github.samarium150.structurescompass.network.StructuresCompassNetwork;
import com.github.samarium150.structurescompass.network.packet.CompassSearchPacket;
import com.github.samarium150.structurescompass.network.packet.CompassSkipExistingChunksPacket;
import com.github.samarium150.structurescompass.util.StructureUtils;
import com.github.samarium150.structurescompass.util.sort.Category;
import com.github.samarium150.structurescompass.util.sort.NameCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
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
    
    private final List<Structure<?>> allowedStructures = StructureUtils.allowedStructures;
    private final ItemStack stack;
    private List<Structure<?>> structuresMatchingSearch;
    private StructureSearchList selectionList;
    private Category category;
    private Button startSearchButton;
    private Button sortByButton;
    private Button skipExistingChunksButton;
    private TextFieldWidget searchTextField;
    private boolean skip;
    private Structure<?> selected;
    
    public StructuresCompassScreen(@Nonnull ItemStack stack) {
        super(new TranslationTextComponent("string.structurescompass.select_structure"));
        this.stack = stack;
        structuresMatchingSearch = new ArrayList<>(allowedStructures);
        category = new NameCategory();
        skip = StructuresCompassItem.isSkip(stack);
    }
    
    private void setup() {
        buttons.clear();
        startSearchButton = addButton(new TransparentButton(
            10, 40, 110, 20,
            I18n.format("string.structurescompass.start_searching"),
            (onPress) -> {
                StructureSearchEntry entry = selectionList.getSelected();
                if (entry != null)
                    entry.search();
            }
        ));
        sortByButton = addButton(new TransparentButton(
            10, 65, 110, 20,
            I18n.format("string.structurescompass.sort_by") + ": " + category.getLocalizedName()
            ,
            (onPress) -> {
                category = category.next();
                sortByButton.setMessage(
                    I18n.format("string.structurescompass.sort_by") + ": " + category.getLocalizedName()
                );
                selectionList.refresh();
                restoreSelected();
            }
        ));
        skipExistingChunksButton = addButton(new TransparentButton(
            10, 90, 110, 20,
            I18n.format("string.structurescompass.skip_existing_chunks") + ": " + skip,
            (onPress) -> {
                skip = !skip;
                skipExistingChunksButton.setMessage(
                    I18n.format("string.structurescompass.skip_existing_chunks") + ": " + skip
                );
            }
        ));
        addButton(new TransparentButton(
            10, height - 30, 110, 20,
            I18n.format("gui.cancel"),
            (onPress) -> {
                assert minecraft != null;
                minecraft.displayGuiScreen(null);
            }
        ));
        searchTextField = new TransparentTextField(
            font, 130, 10, 140, 20,
            I18n.format("string.structurescompass.search")
        );
        children.add(searchTextField);
    }
    
    public void selectStructure(StructureSearchEntry entry) {
        startSearchButton.active = entry != null;
        if (entry != null) selected = entry.getStructure();
    }
    
    public List<Structure<?>> sortStructures() {
        final List<Structure<?>> structures = structuresMatchingSearch;
        structures.sort(new NameCategory());
        structures.sort(category);
        return structuresMatchingSearch;
    }
    
    public void search(@Nonnull Structure<?> structure) {
        assert minecraft != null;
        StructuresCompassNetwork.channel.sendToServer(new CompassSkipExistingChunksPacket(skip));
        StructuresCompassNetwork.channel.sendToServer(new CompassSearchPacket(structure.getRegistryName()));
        minecraft.displayGuiScreen(null);
    }
    
    public void processSearchTerm() {
        structuresMatchingSearch = new ArrayList<>();
        for (Structure<?> structure : allowedStructures) {
            String temp = "";
            if (!searchTextField.getText().isEmpty() && searchTextField.getText().charAt(0) == '#')
                temp = StructureUtils.getDimensions(structure);
            if ((!searchTextField.getText().isEmpty() &&
                    //source search
                    (searchTextField.getText().charAt(0) == '@' &&
                        StructureUtils.getStructureSource(structure).toLowerCase()
                            .contains(searchTextField.getText().substring(1).toLowerCase())) ||
                    //dim search
                    (!temp.isEmpty() && temp.toLowerCase()
                            .contains(searchTextField.getText().substring(1).toLowerCase()))) ||
                //normal search
                (StructureUtils.getLocalizedStructureName(structure).toLowerCase()
                    .contains(searchTextField.getText().toLowerCase()))) {
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
        minecraft.keyboardListener.enableRepeatEvents(true);
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
        children.add(selectionList);
    }
    
    @Override
    public void tick() {
        searchTextField.tick();
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground(0);
        selectionList.render(mouseX, mouseY, partialTicks);
        searchTextField.render(mouseX, mouseY, partialTicks);
        drawCenteredString(font, I18n.format("string.structurescompass.select_structure"), 65, 15, 0xffffff);
        super.render(mouseX, mouseY, partialTicks);
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
    public void onClose() {
        super.onClose();
        assert minecraft != null;
        minecraft.keyboardListener.enableRepeatEvents(false);
    }
}
