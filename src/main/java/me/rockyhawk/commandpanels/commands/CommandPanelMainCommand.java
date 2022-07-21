package me.rockyhawk.commandpanels.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@CommandAlias(Alias.BASE_COMMAND_ALIAS)
//https://rockyhawk99.gitbook.io/rockyhawk-wiki/commandpanels/wiki/commands-and-permissions
public class CommandPanelMainCommand extends BaseCommand implements CommandExecutor {
    final CommandPanels plugin;

    public CommandPanelMainCommand(final CommandPanels plugin) {
        this.plugin = plugin;
    }


    //old start
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //below is going to go through the files and find the right one
        Panel panel = null;
        if (args.length != 0) { //check to make sure the person hasn't just left it empty
            for (Panel tempPanel : plugin.panelList) {
                if (tempPanel.getName().equals(args[0])) {
                    panel = tempPanel;
                    break;
                }
            }
        } else {
            plugin.helpMessage(sender);
            return true;
        }
        if (panel == null) {
            sender.sendMessage(plugin.tex.colour(plugin.tag + plugin.config.getString("config.format.nopanel")));
            return true;
        }
        boolean disableCommand = false;
        if (panel.getConfig().contains("panelType")) {
            if (panel.getConfig().getStringList("panelType").contains("nocommand")) {
                //do not allow command with noCommand, console is an exception
                disableCommand = true;
            }
        }
        //below will start the command, once it got the right file and panel
        if (!cmd.getName().equalsIgnoreCase("cp") || !cmd.getName().equalsIgnoreCase("commandpanel") || !cmd.getName().equalsIgnoreCase("cpanel")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            //do console command command
            if (args.length == 2) {
                if (!args[1].equals("item")) {
                    plugin.openVoids.openCommandPanel(sender, plugin.getServer().getPlayer(args[1]), panel.copy(), PanelPosition.Top, true);
                    return true;
                }

                sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "Usage: /cp <panel> [item] [player]"));
                return true;
            }

            if (args.length == 3) {
                if (args[1].equals("item")) {
                    plugin.openVoids.giveHotbarItem(sender, plugin.getServer().getPlayer(args[2]), panel.copy(), true);
                    return true;
                }

                sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "Usage: /cp <panel> item [player]"));
                return true;
            }

            sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "Please execute command directed to a Player!"));
            return true;

        }
        //get player
        Player p = (Player) sender;
        //do player command
        if (args.length == 1) {
            if (!disableCommand) {
                plugin.openVoids.openCommandPanel(sender, p, panel.copy(), PanelPosition.Top, false);
            }
            return true;
        }
        if (args.length == 2) {
            if (args[1].equals("item")) {
                plugin.openVoids.giveHotbarItem(sender, p, panel.copy(), false);
            } else {
                if (!disableCommand) {
                    plugin.openVoids.openCommandPanel(sender, plugin.getServer().getPlayer(args[1]), panel.copy(), PanelPosition.Top, true);
                }
            }
            return true;
        }
        if (args.length == 3) {
            plugin.openVoids.giveHotbarItem(sender, plugin.getServer().getPlayer(args[2]), panel.copy(), true);
            return true;
        }


        sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "Usage: /cp <panel> [player:item] [player]"));
        return true;
    }
    //old end
}
