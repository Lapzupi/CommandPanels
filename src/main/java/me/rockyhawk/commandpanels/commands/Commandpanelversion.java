package me.rockyhawk.commandpanels.commands;

import me.rockyhawk.commandpanels.CommandPanels;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Commandpanelversion implements CommandExecutor {
    final CommandPanels plugin;
    public Commandpanelversion(CommandPanels pl) { this.plugin = pl; }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (label.equalsIgnoreCase("cpv") || label.equalsIgnoreCase("commandpanelversion") || label.equalsIgnoreCase("cpanelv")) {
            if(args.length == 0) {
                if (sender.hasPermission("commandpanel.version")) {
                    //version command
                    sender.sendMessage(plugin.tex.colour(plugin.tag));
                    sender.sendMessage(ChatColor.GREEN + "This Version   " + ChatColor.GRAY + plugin.getDescription().getVersion());
                    sender.sendMessage(ChatColor.GRAY + "-------------------");
                    sender.sendMessage(ChatColor.GREEN + "Developer " + ChatColor.GRAY + "RockyHawk");
                    sender.sendMessage(ChatColor.GREEN + "Command   " + ChatColor.GRAY + "/cp");
                } else {
                    sender.sendMessage(plugin.tex.colour(plugin.tag + plugin.config.getString("config.format.perms")));
                }
            }
            return true;
        }
        return true;
    }
}
