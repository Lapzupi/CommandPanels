package me.rockyhawk.commandpanels.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias(Alias.BASE_COMMAND_ALIAS)
public class PanelSubCommand extends BaseCommand {
    private final CommandPanels plugin;

    public PanelSubCommand(final CommandPanels plugin) {
        this.plugin = plugin;
    }

    @Subcommand("panel open")
    @CommandAlias("cppo")
    @CommandPermission("commandpanel.panel")
    public void onOpenPanel(final CommandSender sender, final String panelId, @Optional final OnlinePlayer target) {
        final Panel panel = CommandPanels.getAPI().getPanel(panelId);
        if (panel == null) {
            sender.sendMessage(plugin.tex.colour(plugin.tag + plugin.config.getString("config.format.nopanel")));
            return;
        }

        if (panel.getConfig().contains("panelType")
                && panel.getConfig().getStringList("panelType").contains("nocommand")
                && !sender.hasPermission("commandpanel.panel.bypass.nocommand")) {
            //do not allow command with noCommand, console is an exception
            if (sender.hasPermission("commandpanel.panel.notify.nocommand")) {
                sender.sendMessage(plugin.tex.colour(plugin.tag + "Cannot open this panel since it has no-command enabled."));
            }
            return;
        }

        //Console
        if (!(sender instanceof Player player)) {
            if (target == null) {
                sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "You must specify a target player when executing from console."));
                return;
            }

            plugin.openVoids.openCommandPanel(sender, target.getPlayer(), panel.copy(), PanelPosition.Top, true);
            return;
        }

        //Player
        if (target == null) {
            plugin.openVoids.openCommandPanel(sender, player, panel.copy(), PanelPosition.Top, false);
            return;
        }

        if(!sender.hasPermission("commandpanel.panel.other")) {
            sender.sendMessage("You do not have permission to open panels for other players.");
            return;
        }
        plugin.openVoids.openCommandPanel(sender, target.getPlayer(), panel.copy(), PanelPosition.Top, true);
    }

    @Subcommand("panel item")
    @CommandAlias("cppi")
    public void onGivePanelItem(final CommandSender sender, final String panelId, @Optional final OnlinePlayer target) {
        final Panel panel = CommandPanels.getAPI().getPanel(panelId);
        if (panel == null) {
            sender.sendMessage(plugin.tex.colour(plugin.tag + plugin.config.getString("config.format.nopanel")));
            return;
        }

        //Console
        if (!(sender instanceof Player player)) {
            if (target == null) {
                sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "You must specify a target player when executing from console."));
                return;
            }

            plugin.openVoids.giveHotbarItem(sender, target.getPlayer(), panel.copy(),  true);
            return;
        }

        //Player
        if (target == null) {
            plugin.openVoids.giveHotbarItem(sender, player, panel.copy(),  true);
            return;
        }

        if(!sender.hasPermission("commandpanel.panel.other")) {
            sender.sendMessage("You do not have permission to open panels for other players.");
            return;
        }
        plugin.openVoids.giveHotbarItem(sender, target.getPlayer(), panel.copy(),  true);
    }
}
