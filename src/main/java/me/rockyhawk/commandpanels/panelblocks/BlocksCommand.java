package me.rockyhawk.commandpanels.panelblocks;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.commands.Alias;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@CommandAlias(Alias.BASE_COMMAND_ALIAS)
public class BlocksCommand extends BaseCommand {
    private final CommandPanels plugin;

    public BlocksCommand(final CommandPanels plugin) {
        this.plugin = plugin;
    }

    @Subcommand("block")
    @CommandAlias("cpb")
    public class BlockSubCommand extends BaseCommand {


        @Subcommand("add")
        @CommandPermission("commandpanel.block.add")
        public void onAdd(final Player sender, final String panelId) {
            if (Objects.requireNonNull(plugin.config.getString("config.panel-blocks")).equalsIgnoreCase("false")) {
                sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "Panel blocks disabled in config!"));
                return;
            }
            boolean foundPanel = false;
            for (Panel temp : plugin.panelList) {
                if (temp.getName().equals(panelId)) {
                    foundPanel = true;
                    break;
                }
            }
            if (!foundPanel) {
                sender.sendMessage(plugin.tex.colour(plugin.tag + plugin.config.getString("config.format.nopanel")));
                return;
            }
            Block blockType = sender.getTargetBlock(null, 5);
            if (blockType.getType() == Material.AIR) {
                sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "Look at a block to add a panel!"));
                return;
            }
            Location blockLocation = blockType.getLocation();
            String configValue = "blocks." + Objects.requireNonNull(blockLocation.getWorld()).getName().replace("_", "%dash%") + "_" + blockLocation.getBlockX() + "_" + blockLocation.getBlockY() + "_" + blockLocation.getBlockZ() + ".panel";
            plugin.blockConfig.set(configValue, panelId);
            try {
                plugin.blockConfig.save(new File(plugin.getDataFolder() + File.separator + "blocks.yml"));
            } catch (IOException e) {
                plugin.debug(e, sender);
                sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "Could not save to file!"));
                return;
            }
            //make the material name look okay
            String coordinates = blockLocation.getBlockX() + ", " + blockLocation.getBlockY() + ", " + blockLocation.getBlockZ();
            sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.WHITE + panelId) + ChatColor.GREEN + "will now open when right clicking a block in the coordinates " + ChatColor.WHITE + coordinates);
        }

        @Subcommand("remove")
        @CommandPermission("commandpanel.block.remove")
        public void onRemove(final Player sender) {
            if (Objects.requireNonNull(plugin.config.getString("config.panel-blocks")).equalsIgnoreCase("false")) {
                sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "Panel blocks disabled in config!"));
                return;
            }
            Block blockType = sender.getTargetBlock(null, 5);
            Location blockLocation = blockType.getLocation();
            String configValue = "blocks." + Objects.requireNonNull(blockLocation.getWorld()).getName().replace("_", "%dash%") + "_" + blockLocation.getBlockX() + "_" + blockLocation.getBlockY() + "_" + blockLocation.getBlockZ() + ".panel";
            if (!plugin.blockConfig.contains(configValue)) {
                sender.sendMessage(plugin.tex.colour(plugin.tag + plugin.config.getString("config.format.nopanel")));
                return;
            }
            plugin.blockConfig.set(configValue.replace(".panel", ""), null);
            try {
                plugin.blockConfig.save(new File(plugin.getDataFolder() + File.separator + "blocks.yml"));
            } catch (IOException e) {
                plugin.debug(e, sender);
                sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.RED + "Could not save to file!"));
                return;
            }
            sender.sendMessage(plugin.tex.colour(plugin.tag + ChatColor.GREEN + "Panel has been removed from block."));

        }

        @Subcommand("list")
        @CommandPermission("commandpanel.block.list")
        public void onList(final Player sender) {
            if (!plugin.blockConfig.contains("blocks")) {
                sender.sendMessage(plugin.tex.colour(plugin.tag) + ChatColor.RED + "No panel blocks found.");
                return;
            }

            if (Objects.requireNonNull(plugin.blockConfig.getConfigurationSection("blocks")).getKeys(false).isEmpty()) {
                sender.sendMessage(plugin.tex.colour(plugin.tag) + ChatColor.RED + "No panel blocks found.");
                return;
            }
            sender.sendMessage(plugin.tex.colour(plugin.tag) + ChatColor.DARK_AQUA + "Panel Block Locations:");
            for (String location : Objects.requireNonNull(plugin.blockConfig.getConfigurationSection("blocks")).getKeys(false)) {
                sender.sendMessage(ChatColor.GREEN + location.replace("_", " ") + ": " + ChatColor.WHITE + plugin.blockConfig.getString("blocks." + location + ".panel"));
            }

        }
    }
}
