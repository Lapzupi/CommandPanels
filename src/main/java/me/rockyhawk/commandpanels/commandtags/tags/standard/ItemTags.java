package me.rockyhawk.commandpanels.commandtags.tags.standard;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.commandtags.CommandTagEvent;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ItemTags implements Listener {
    final CommandPanels plugin;

    public ItemTags(CommandPanels pl) {
        this.plugin = pl;
    }

    @EventHandler
    public void commandTag(CommandTagEvent event) {
        if (event.name.equalsIgnoreCase("give-item=")) {
            event.commandTagUsed();
            ItemStack itm = plugin.itemCreate.makeCustomItemFromConfig(null, event.pos, event.panel.getConfig().getConfigurationSection("custom-item." + event.args[0]), event.p, true, true, false);
            plugin.inventorySaver.addItem(event.p, itm);
            return;
        }
        if (event.name.equalsIgnoreCase("setitem=")) {
            event.commandTagUsed();
            //if player uses setitem= [custom item] [slot] [position] it will change the item slot to something, used for placeable items
            //make a section in the panel called "custom-item" then whatever the title of the item is, put that here
            ItemStack s = plugin.itemCreate.makeItemFromConfig(null, event.pos, event.panel.getConfig().getConfigurationSection("custom-item." + event.args[0]), event.p, true, true, true);
            PanelPosition position = PanelPosition.valueOf(event.args[2]);
            if (position == PanelPosition.Top) {
                event.p.getOpenInventory().getTopInventory().setItem(Integer.parseInt(event.args[1]), s);
            } else if (position == PanelPosition.Middle) {
                event.p.getInventory().setItem(Integer.parseInt(event.args[1]) + 9, s);
            } else {
                event.p.getInventory().setItem(Integer.parseInt(event.args[1]), s);
            }
        }
    }
}
