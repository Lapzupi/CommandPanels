package me.rockyhawk.commandpanels.commandtags.tags.standard;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.PanelCommandEvent;
import me.rockyhawk.commandpanels.classresources.SerializerUtils;
import me.rockyhawk.commandpanels.commandtags.CommandTagEvent;
import me.rockyhawk.commandpanels.ioclasses.legacy.MinecraftVersions;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelOpenType;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BasicTags implements Listener {
    final CommandPanels plugin;

    public BasicTags(CommandPanels pl) {
        this.plugin = pl;
    }

    @EventHandler
    public void commandTag(CommandTagEvent event) {
        if (event.name.equalsIgnoreCase("cpc")) {
            event.commandTagUsed();

            //unclosable panels are at the Top only
            if (plugin.openPanels.getOpenPanel(event.p.getName(), PanelPosition.Top).getConfig().contains("panelType")) {
                if (plugin.openPanels.getOpenPanel(event.p.getName(), PanelPosition.Top).getConfig().getStringList("panelType").contains("unclosable")) {
                    plugin.openPanels.closePanelForLoader(event.p.getName(), PanelPosition.Top);
                    plugin.openPanels.skipPanelClose.add(event.p.getName());
                }
            }

            //this will close the current inventory
            event.p.closeInventory();
            plugin.openPanels.skipPanelClose.remove(event.p.getName());
            return;
        }
        if (event.name.equalsIgnoreCase("refresh")) {
            event.commandTagUsed();
            if (plugin.openPanels.hasPanelOpen(event.p.getName(), event.pos)) {
                plugin.createGUI.openGui(event.panel, event.p, event.pos, PanelOpenType.Refresh, 0);
            }
            if (plugin.inventorySaver.hasNormalInventory(event.p)) {
                plugin.hotbar.updateHotbarItems(event.p);
            }
            return;
        }
        if (event.name.equalsIgnoreCase("console=")) {
            event.commandTagUsed();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.join(" ", event.args));
            return;
        }
        if (event.name.equalsIgnoreCase("send=")) {
            event.commandTagUsed();
            event.p.chat(String.join(" ", event.args));
            return;
        }
        if (event.name.equalsIgnoreCase("sudo=")) {
            event.commandTagUsed();
            event.p.chat("/" + String.join(" ", event.args));
            return;
        }
        if (event.name.equalsIgnoreCase("msg=")) {
            event.commandTagUsed();
            plugin.tex.sendString(event.panel, event.pos, event.p, String.join(" ", event.args));
            return;
        }
        if (event.name.equalsIgnoreCase("op=")) {
            event.commandTagUsed();
            //if player uses op= it will perform command as op
            boolean isop = event.p.isOp();
            try {
                event.p.setOp(true);
                Bukkit.dispatchCommand(event.p, String.join(" ", event.args));
                event.p.setOp(isop);
            } catch (Exception exc) {
                event.p.setOp(isop);
                plugin.debug(exc, event.p);
                event.p.sendMessage(plugin.tag + plugin.tex.colour(plugin.config.getString("config.format.error") + " op=: Error in op command!"));
            }
            return;
        }
        if (event.name.equalsIgnoreCase("sound=")) {
            event.commandTagUsed();
            try {
                if (event.args.length == 3) {
                    //volume (0.0 to 1.0), pitch (0.5 to 2.0)
                    event.p.playSound(event.p.getLocation(), Sound.valueOf(event.args[0]), Float.parseFloat(event.args[1]), Float.parseFloat(event.args[2]));
                } else {
                    event.p.playSound(event.p.getLocation(), Sound.valueOf(event.args[0]), 1F, 1F);
                }
            } catch (Exception s) {
                plugin.debug(s, event.p);
                plugin.tex.sendMessage(event.p, plugin.config.getString("config.format.error") + " " + "commands: " + event.args[0]);
            }
            return;
        }
        if (event.name.equalsIgnoreCase("stopsound=")) {
            event.commandTagUsed();
            try {
                event.p.stopSound(Sound.valueOf(event.args[0]));
            } catch (Exception ss) {
                plugin.debug(ss, event.p);
                plugin.tex.sendMessage(event.p, plugin.config.getString("config.format.error") + " " + "commands: " + event.args[0]);
            }
            return;
        }
        if (event.name.equalsIgnoreCase("event=")) {
            event.commandTagUsed();
            PanelCommandEvent commandEvent = new PanelCommandEvent(event.p, String.join(" ", event.args), event.panel);
            Bukkit.getPluginManager().callEvent(commandEvent);
            return;
        }
        if (event.name.equalsIgnoreCase("minimessage=")) {
            event.commandTagUsed();
            if (plugin.legacy.LOCAL_VERSION.greaterThanOrEqualTo(MinecraftVersions.v1_18) && Bukkit.getServer().getVersion().contains("Paper")) {
                Audience player = event.p; // Needed because the basic Player from the Event can't send Paper's Components
                Component parsedText = SerializerUtils.serializeText(String.join(" ", event.args));
                player.sendMessage(parsedText);
            } else {
                plugin.tex.sendString(event.p, plugin.tag + ChatColor.RED + "MiniMessage-Feature needs Paper 1.18 or newer to work!");
            }
        }
    }
}