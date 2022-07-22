package me.rockyhawk.commandpanels.openpanelsmanager;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.api.PanelClosedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class UtilsPanelsLoader implements Listener {
    final CommandPanels plugin;

    public UtilsPanelsLoader(CommandPanels pl) {
        this.plugin = pl;
    }

    //tell panel loader that player has opened panel
    @EventHandler
    public void onPlayerClosePanel(PlayerQuitEvent e) {
        plugin.openPanels.closePanelForLoader(e.getPlayer().getName(), PanelPosition.TOP);
        Player p = e.getPlayer();
        p.updateInventory();
        for (ItemStack itm : p.getInventory().getContents()) {
            if (itm != null) {
                if (plugin.nbt.hasNBT(itm)) {
                    p.getInventory().remove(itm);
                }
            }
        }
    }

    //tell panel loader that player has closed the panel (there is also one of these in EditorUtils)
    @EventHandler
    public void onPlayerClosePanel(InventoryCloseEvent e) {
        String playerName = e.getPlayer().getName();

        //close if not panel
        if (!plugin.openPanels.openPanels.containsKey(playerName) || plugin.openPanels.skipPanelClose.contains(playerName)) {
            return;
        }

        //check for panelType unclosable (unclosable is Top only)
        if (plugin.openPanels.getOpenPanel(playerName, PanelPosition.TOP).getConfig().contains("panelType")) {
            if (plugin.openPanels.getOpenPanel(playerName, PanelPosition.TOP).getConfig().getStringList("panelType").contains("unclosable")) {
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.openPanels.getOpenPanel(playerName, PanelPosition.TOP).open(Bukkit.getPlayer(playerName), PanelPosition.TOP));
                return;
            }
        }

        //run commands-on-close for panels
        if (plugin.openPanels.hasPanelOpen(e.getPlayer().getName(), PanelPosition.BOTTOM)) {
            plugin.openPanels.panelCloseCommands(playerName, PanelPosition.BOTTOM, plugin.openPanels.getOpenPanel(playerName, PanelPosition.BOTTOM));
        }
        if (plugin.openPanels.hasPanelOpen(e.getPlayer().getName(), PanelPosition.MIDDLE)) {
            plugin.openPanels.panelCloseCommands(playerName, PanelPosition.MIDDLE, plugin.openPanels.getOpenPanel(playerName, PanelPosition.MIDDLE));
        }

        //close panels and run commands for Top panel
        plugin.openPanels.closePanelForLoader(e.getPlayer().getName(), PanelPosition.TOP);
    }

    @EventHandler
    public void onInventoryItemClick(InventoryClickEvent e) {
        //this will check to ensure an item is not from CommandPanels on inventory open
        Player p = (Player) e.getWhoClicked();
        if (!plugin.openPanels.hasPanelOpen(p.getName(), PanelPosition.TOP)) {
            for (ItemStack itm : p.getInventory().getContents()) {
                if (plugin.openPanels.isNBTInjected(itm)) {
                    p.getInventory().remove(itm);
                }
            }
        }
    }

    //if the regular InventoryOpenEvent is called
    @EventHandler(priority = EventPriority.HIGHEST)
    public void vanillaOpenedEvent(InventoryOpenEvent event) {
        if (event.isCancelled() || !plugin.openPanels.hasPanelOpen(event.getPlayer().getName(), PanelPosition.TOP)) {
            return;
        }

        Panel closedPanel = plugin.openPanels.getOpenPanel(event.getPlayer().getName(), PanelPosition.TOP);

        //manually remove player with no skip checks
        plugin.openPanels.removePlayer(event.getPlayer().getName());

        //fire PanelClosedEvent
        PanelClosedEvent closedEvent = new PanelClosedEvent(Bukkit.getPlayer(event.getPlayer().getName()), closedPanel, PanelPosition.TOP);
        Bukkit.getPluginManager().callEvent(closedEvent);

        //do message
        if (plugin.config.contains("config.panel-snooper")) {
            if (Objects.requireNonNull(plugin.config.getString("config.panel-snooper")).equalsIgnoreCase("true")) {
                Bukkit.getConsoleSender().sendMessage("[CommandPanels] " + event.getPlayer().getName() + "'s Panel was Force Closed");
            }
        }


    }
}
