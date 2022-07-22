package me.rockyhawk.commandpanels.commandtags.tags.other;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.commandtags.CommandTagEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DataTags implements Listener {
    final CommandPanels plugin;

    public DataTags(CommandPanels pl) {
        this.plugin = pl;
    }

    @EventHandler
    public void commandTag(CommandTagEvent event) {
        if (event.name.equalsIgnoreCase("set-data=")) {
            event.commandTagUsed();
            if (event.args.length == 3) {
                plugin.panelData.setUserData(plugin.panelData.getOffline(event.args[2]), event.args[0], plugin.tex.placeholdersNoColour(event.panel, event.pos, event.p, event.args[1]), true);
                return;
            }
            //this will overwrite data. set-data= [data point] [data value] [optional player]
            plugin.panelData.setUserData(event.p.getUniqueId(), event.args[0], plugin.tex.placeholdersNoColour(event.panel, event.pos, event.p, event.args[1]), true);
            return;
        }
        if (event.name.equalsIgnoreCase("add-data=")) {
            event.commandTagUsed();
            if (event.args.length == 3) {
                plugin.panelData.setUserData(plugin.panelData.getOffline(event.args[2]), event.args[0], plugin.tex.placeholdersNoColour(event.panel, event.pos, event.p, event.args[1]), false);
                return;
            }
            //this will not overwrite existing data. add-data= [data point] [data value] [optional player]
            plugin.panelData.setUserData(event.p.getUniqueId(), event.args[0], plugin.tex.placeholdersNoColour(event.panel, event.pos, event.p, event.args[1]), false);
            return;
        }
        if (event.name.equalsIgnoreCase("math-data=")) {
            event.commandTagUsed();
            if (event.args.length == 3) {
                plugin.panelData.doDataMath(plugin.panelData.getOffline(event.args[2]), event.args[0], plugin.tex.placeholdersNoColour(event.panel, event.pos, event.p, event.args[1]));
                return;
            }
            //only works if data is number, goes math-data= [data point] [operator:number] [optional player] eg, math-data= -1 OR /3
            plugin.panelData.doDataMath(event.p.getUniqueId(), event.args[0], plugin.tex.placeholdersNoColour(event.panel, event.pos, event.p, event.args[1]));
            return;
        }
        if (event.name.equalsIgnoreCase("clear-data=")) {
            event.commandTagUsed();
            //will clear all data for player clear-data= [playerName]
            plugin.panelData.clearData(event.p.getUniqueId());
            return;
        }
        if (event.name.equalsIgnoreCase("del-data=")) {
            event.commandTagUsed();
            if (event.args.length == 3) {
                plugin.panelData.delUserData(plugin.panelData.getOffline(event.args[1]), event.args[0]);
                return;
            }
            //this will remove data. del-data= [data point] [optional player]
            plugin.panelData.delUserData(event.p.getUniqueId(), event.args[0]);
        }
    }
}
