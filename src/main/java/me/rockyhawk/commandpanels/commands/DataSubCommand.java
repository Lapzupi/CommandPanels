package me.rockyhawk.commandpanels.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import me.rockyhawk.commandpanels.CommandPanels;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias(Alias.BASE_COMMAND_ALIAS)
public class DataSubCommand extends BaseCommand {
    protected CommandPanels plugin;

    public DataSubCommand(final CommandPanels plugin) {
        this.plugin = plugin;
    }

    @Subcommand("data")
    @CommandPermission("commandpanel.data")
    public class DataCommand extends BaseCommand {

        @Subcommand("clear")
        public void onClear(final CommandSender sender, final Player target, @Optional final SilentTag silentTag) {
            plugin.panelData.clearData(plugin.panelData.getOffline(target.getName()));
            if (silentTag == SilentTag.NONE) {
                sender.sendMessage(plugin.tex.colour(plugin.tag
                        + ChatColor.GREEN + "Cleared all data for "
                        + ChatColor.WHITE + target.getName()));
            }
        }

        @Subcommand("remove")
        public void onRemove(final CommandSender sender, final Player target, final String dataPoint, @Optional final SilentTag silentTag) {
            plugin.panelData.delUserData(plugin.panelData.getOffline(target.getName()), dataPoint);
            if (silentTag == SilentTag.NONE) {
                sender.sendMessage(plugin.tex.colour(plugin.tag
                        + ChatColor.GREEN + "Removed "
                        + ChatColor.WHITE + dataPoint
                        + ChatColor.GREEN + " from "
                        + ChatColor.WHITE + target.getName()));
            }
        }

        @Subcommand("get")
        public void onGet(final CommandSender sender, final Player target, final String dataPoint) {
            sender.sendMessage(plugin.tex.colour(plugin.tag
                    + ChatColor.GREEN + "Value of data is "
                    + ChatColor.WHITE + plugin.panelData.getUserData(plugin.panelData.getOffline(target.getName()), dataPoint)));
        }

        @Subcommand("set")
        public void onSet(final CommandSender sender, final Player target, final String dataPoint, final String dataValue, @Optional final SilentTag silentTag){
            plugin.panelData.setUserData(plugin.panelData.getOffline(target.getName()), dataPoint, dataValue, true);
            if (silentTag == SilentTag.NONE) {
                sender.sendMessage(plugin.tex.colour(plugin.tag
                        + ChatColor.GREEN + "Set "
                        + ChatColor.WHITE + dataPoint
                        + ChatColor.GREEN + " to "
                        + ChatColor.WHITE + dataValue));
            }
        }

        @Subcommand("add")
        public void onAdd(final CommandSender sender, final Player target, final String dataPoint, final String dataValue,@Optional final SilentTag silentTag) {
            //for add command
            plugin.panelData.setUserData(plugin.panelData.getOffline(target.getName()), dataPoint, dataValue, false);
            if (silentTag == SilentTag.NONE) {
                sender.sendMessage(plugin.tex.colour(plugin.tag
                        + ChatColor.GREEN + "Set "
                        + ChatColor.WHITE + dataPoint
                        + ChatColor.GREEN + " to "
                        + ChatColor.WHITE + dataValue)
                        + ChatColor.GREEN + " if it did not exist already");
            }
        }
    }

        public enum SilentTag {
            SILENT,
            NONE;
        }
    }
