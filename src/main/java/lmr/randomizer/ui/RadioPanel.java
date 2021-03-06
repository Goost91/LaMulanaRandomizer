package lmr.randomizer.ui;

import lmr.randomizer.Main;
import lmr.randomizer.Settings;
import lmr.randomizer.Translations;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RadioPanel extends JPanel {
    List<GameItemRadio> itemConfigRadioGroupPanels;

    public RadioPanel() {
        super(new MigLayout("fillx, wrap 6", "", "[fill]"));
        setBorder(BorderFactory.createTitledBorder(Translations.getText("settings.randomization.items")));

        itemConfigRadioGroupPanels = new ArrayList<>();
        itemConfigRadioGroupPanels.add(new GameItemRadio("Holy Grail"));
        itemConfigRadioGroupPanels.add(new GameItemRadio("Hand Scanner"));
        itemConfigRadioGroupPanels.add(new GameItemRadio("reader.exe"));
        itemConfigRadioGroupPanels.add(new GameItemRadio("Hermes' Boots"));
        itemConfigRadioGroupPanels.add(new GameItemRadio("Grapple Claw"));
        itemConfigRadioGroupPanels.add(new GameItemRadio("Feather"));

        itemConfigRadioGroupPanels.add(new GameItemRadio("Isis' Pendant"));
        itemConfigRadioGroupPanels.add(new GameItemRadio("Bronze Mirror"));
        itemConfigRadioGroupPanels.add(new GameItemRadio("mirai.exe"));
        itemConfigRadioGroupPanels.add(new GameItemRadio("bunemon.exe"));

        for(GameItemRadio gameItemRadio : itemConfigRadioGroupPanels) {
            add(gameItemRadio);
        }
    }

    public void updateTranslations() {
        for(GameItemRadio gameItemRadio : itemConfigRadioGroupPanels) {
            gameItemRadio.updateTranslations();
        }
        setBorder(BorderFactory.createTitledBorder(Translations.getText("settings.randomization.items")));
    }

    public void updateSettings() {
        Set<String> initiallyAvailableItems = new HashSet<>();
        Set<String> nonRandomizedItems = new HashSet<>();
        Set<String> surfaceItems = new HashSet<>();

        for(GameItemRadio itemRadio : itemConfigRadioGroupPanels) {
            String actionCommand = itemRadio.getActionCommand();
            if("INITIAL".equals(actionCommand)) {
                Main.addArgItemUI(initiallyAvailableItems, itemRadio.getItemName());
            }
            else if("NONRANDOM".equals(actionCommand)) {
                Main.addArgItemUI(nonRandomizedItems, itemRadio.getItemName());
            }
            else if("V_EARLY".equals(actionCommand)) {
                Main.addArgItemUI(surfaceItems, itemRadio.getItemName());
            }
        }
        Settings.setInitiallyAccessibleItems(initiallyAvailableItems, true);
        Settings.setNonRandomizedItems(nonRandomizedItems, true);
        Settings.setSurfaceItems(surfaceItems, true);
    }

    public void reloadSettings() {
        Set<String> initiallyAvailableItems = Settings.getInitiallyAccessibleItems();
        Set<String> nonRandomizedItems = Settings.getNonRandomizedItems();
        Set<String> surfaceItems = Settings.getSurfaceItems();

        for(GameItemRadio itemRadio : itemConfigRadioGroupPanels) {
            String item = itemRadio.getItemName();
            if(initiallyAvailableItems.contains(item)) {
                itemRadio.setSelected("INITIAL");
            } else if(surfaceItems.contains(item)) {
                itemRadio.setSelected("SURFACE");
            } else if(nonRandomizedItems.contains(item)) {
                itemRadio.setSelected("NONRANDOM");
            } else {
                itemRadio.setSelected("RANDOM");
            }

        }
    }
}
