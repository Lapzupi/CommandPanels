package me.rockyhawk.commandpanels.datamanager;

import me.rockyhawk.commandpanels.CommandPanels;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class DebugManager {
    final CommandPanels plugin;
    public DebugManager(CommandPanels pl) { this.plugin = pl; }

    public final Set<Player> debugSet = new HashSet<>();
    public boolean consoleDebug = false;

    public boolean isEnabled(Player p){
        return debugSet.contains(p);
    }

    public boolean isEnabled(CommandSender sender){
        if(sender instanceof Player player){
            return isEnabled(player);
        }
        return consoleDebug;
    }
}
