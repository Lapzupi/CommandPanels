package me.rockyhawk.commandpanels.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@CommandAlias(Alias.BASE_COMMAND_ALIAS)
public class MainCommand extends BaseCommand {
    private final CommandPanels plugin;

    public MainCommand(final CommandPanels plugin) {
        this.plugin = plugin;
    }

    @Subcommand("version")
    @CommandPermission("commandpanel.version")
    public void onVersion(final CommandSender sender) {
        //version command
        sender.sendMessage(plugin.tex.colour(plugin.tag));
        sender.sendMessage(ChatColor.GREEN + "This Version   " + ChatColor.GRAY + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GRAY + "-------------------");
        sender.sendMessage(ChatColor.GREEN + "Developer " + ChatColor.GRAY + "RockyHawk");
        sender.sendMessage(ChatColor.GREEN + "Command   " + ChatColor.GRAY + "/cp");
    }

    @Subcommand("debug")
    @CommandPermission("commandpanel.debug")
    public void onDebug(final CommandSender sender) {
        if (!(sender instanceof Player player)) {
            plugin.debug.consoleDebug = !plugin.debug.consoleDebug;
            sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.GREEN + "Global Debug Mode: " + plugin.debug.consoleDebug));
            return;
        }

        if (plugin.debug.isEnabled(player)) {
            plugin.debug.debugSet.remove(player);
            sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.GREEN + "Personal Debug Mode Disabled!"));
            return;
        }

        plugin.debug.debugSet.add(player);
        sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.GREEN + "Personal Debug Mode Enabled!"));
    }

    @Subcommand("generate")
    @CommandAlias("cpg")
    @CommandCompletion("@range:1-6") //todo enforce this
    @CommandPermission("commandpanel.generate")
    public void onGenerate(final Player player,@Optional final Integer rows) {
        if(rows == null) {
            if (this.plugin.generateMode.contains(player)) {
                this.plugin.generateMode.remove(player);
                player.sendMessage(plugin.tex.colour( plugin.tag + ChatColor.GREEN + "Generate Mode Disabled!"));
            } else {
                this.plugin.generateMode.add(player);
                player.sendMessage(plugin.tex.colour( plugin.tag + ChatColor.GREEN + "Generate Mode Enabled!"));
            }
            return;
        }
        Inventory i = Bukkit.createInventory(player, rows * 9, "Generate New Panel");
        player.openInventory(i);

    }

    @Subcommand("reload")
    @CommandPermission("commandpanel.reload")
    public void onReload(final CommandSender sender) {
        //close all the panels
        for (String name : plugin.openPanels.openPanels.keySet()) {
            plugin.openPanels.closePanelForLoader(name, PanelPosition.TOP);
            try {
                Bukkit.getPlayer(name).closeInventory();
            } catch (Exception ignore) {
                //ignored
            }
        }

        plugin.reloadPanelFiles();
        try {
            Files.delete(Paths.get(plugin.getDataFolder() + File.separator + "temp.yml"));
        } catch (IOException e) {
            //log
        }
        plugin.getDefaultConfig().reloadConfig();
        plugin.getBlockConfig().reloadConfig();

        //check for duplicates
        plugin.checkDuplicatePanel(sender);

        //reloadHotbarSlots
        plugin.hotbar.reloadHotbarSlots();

        //reload tag
        plugin.tag = plugin.tex.colour(plugin.getDefaultConfig().getConfig().getString("config.format.tag"));

        //add custom commands to commands.yml
        if (plugin.getDefaultConfig().getConfig().getBoolean("config.auto-register-commands")) {
            registerCommands();
        }

        sender.sendMessage(plugin.tex.colour(plugin.tag + plugin.getDefaultConfig().getConfig().getString("config.format.reload")));
    }

    public void registerCommands() {
        File commandsLoc = new File("commands.yml");
        YamlConfiguration cmdCF;
        try {
            cmdCF = YamlConfiguration.loadConfiguration(commandsLoc);
        } catch (Exception e) {
            //could not access the commands.yml file
            plugin.debug(e, null);
            return;
        }

        //remove old commandpanels commands
        for (String existingCommands : cmdCF.getConfigurationSection("aliases").getKeys(false)) {
            try {
                if (cmdCF.getStringList("aliases." + existingCommands).get(0).equals("commandpanel")) {
                    cmdCF.set("aliases." + existingCommands, null);
                }
            } catch (Exception ignore) {
            }
        }

        //make the command 'commandpanels' to identify it
        ArrayList<String> temp = new ArrayList<>();
        temp.add("commandpanel");

        for (Panel panel : plugin.panelList) {
            if (panel.getConfig().contains("panelType")) {
                if (panel.getConfig().getStringList("panelType").contains("nocommandregister")) {
                    continue;
                }
            }

            if (panel.getConfig().contains("commands")) {
                List<String> panelCommands = panel.getConfig().getStringList("commands");
                for (String command : panelCommands) {
                    cmdCF.set("aliases." + command.split("\\s")[0], temp);
                }
            }
        }

        try {
            cmdCF.save(commandsLoc);
        } catch (IOException var10) {
            Bukkit.getConsoleSender().sendMessage("[CommandPanels]" + ChatColor.RED + " WARNING: Could not register custom commands!");
        }
    }
}
