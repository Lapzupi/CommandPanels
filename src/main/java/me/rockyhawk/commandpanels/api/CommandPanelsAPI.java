package me.rockyhawk.commandpanels.api;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandPanelsAPI {
    //set to public to adjust any public settings within the plugin through the API
    public final CommandPanels plugin;

    public CommandPanelsAPI(CommandPanels pl) {
        this.plugin = pl;
    }

    //returns true if the player has a panel open
    public boolean isPanelOpen(@NotNull Player player) {
        return plugin.openPanels.hasPanelOpen(player.getName(), PanelPosition.TOP);
    }

    //get the name of a panel currently open, will return null if panel is not open
    public Panel getOpenPanel(@NotNull Player player, PanelPosition position) {
        return plugin.openPanels.getOpenPanel(player.getName(), position);
    }

    //loaded panels in folder
    public List<Panel> getPanelsLoaded() {
        return plugin.panelList;
    }

    //import panel into folder
    public void addPanel(@NotNull Panel panel) throws IOException {
        File addedFile = new File(plugin.panelsFolder + File.separator + panel.getName() + ".yml");
        YamlConfiguration newYaml = new YamlConfiguration();
        if (panel.getConfig().contains("panels")) {
            newYaml.set("", panel.getConfig());
        } else {
            newYaml.set("panels." + panel.getName(), panel.getConfig());
        }
        newYaml.save(addedFile);
        plugin.reloadPanelFiles();
    }

    //remove panel from folder
    public void removePanel(Panel panel) {
        for (Panel panels : plugin.panelList) {
            if (panels.getName().equals(panel.getName())) {
                if (panels.getFile().delete()) {
                    plugin.reloadPanelFiles();
                }
            }
        }
    }

    //get panel from folder
    public Panel getPanel(String panelName) {
        for (Panel panel : plugin.panelList) {
            if (panel.getName().equals(panelName)) {
                return panel;
            }
        }
        return null;
    }

    //if the players inventory has no panels in it
    public boolean hasNormalInventory(Player p) {
        return plugin.inventorySaver.hasNormalInventory(p);
    }

    //make custom item using items section
    public ItemStack makeItem(Player p, ConfigurationSection itemSection) {
        return plugin.itemCreate.makeCustomItemFromConfig(null, PanelPosition.TOP, itemSection, p, true, true, false);
    }
}