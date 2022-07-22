package me.rockyhawk.commandpanels.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.Panel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

@CommandAlias(Alias.BASE_COMMAND_ALIAS)
public class ListSubCommand extends BaseCommand {
    private final CommandPanels plugin;

    public ListSubCommand(final CommandPanels plugin) {
        this.plugin = plugin;
    }

    @Subcommand("list")
    @CommandAlias("cpl")
    @CommandPermission("commandpanel.list")
    public void onList(final CommandSender sender, Integer page) {
        ArrayList<Panel> panels = new ArrayList<>(plugin.panelList);
        int skip = 0;
        if (page != null) {
            try {
                skip = page * 8 - 8;
            } catch (Exception e) {
                sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "Inaccessible Page"));
            }
        } else {
            page = 1;
        }

        sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.DARK_AQUA + "Panels: (Page " + page + ")"));
        for (int f = skip; panels.size() > f && skip + 8 > f; f++) {
            sender.sendMessage(ChatColor.DARK_GREEN + panels.get(f).getFile().getAbsolutePath().replace(plugin.panelsFolder.getAbsolutePath(), "") + ChatColor.GREEN + " " + panels.get(f).getName());
            if (panels.size() - 1 == f) {
                return;
            }
        }
        sender.sendMessage(ChatColor.AQUA + "Type /cpl " + (page + 1) + " to read next page");
    }
}
