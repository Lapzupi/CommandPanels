package me.rockyhawk.commandpanels.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rockyhawk.commandpanels.CommandPanels;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

//todo make async https://www.baeldung.com/java-download-file
@CommandAlias(Alias.BASE_COMMAND_ALIAS)
public class ImportSubCommand extends BaseCommand {
    private final CommandPanels plugin;

    public ImportSubCommand(final CommandPanels plugin) {
        this.plugin = plugin;
    }

    @Subcommand("import")
    @CommandPermission("commandpanel.import")
    public void onImport(final CommandSender sender, final String fileName, final String url) {
        new BukkitRunnable() {
            @Override
            public void run() {
                downloadPanel(sender, url, fileName);
                plugin.reloadPanelFiles();
            }
        }.runTask(plugin);
    }


    private void downloadPanel(CommandSender sender, String url, String fileName) {
        try {
            URL fileUrl = new URL(url);
            try (BufferedInputStream in = new BufferedInputStream(fileUrl.openStream())) {
                try (FileOutputStream fout = new FileOutputStream(new File(plugin.panelsFolder, fileName + ".yml"))) {
                    byte[] data = new byte[1024];

                    int count;
                    while ((count = in.read(data, 0, 1024)) != -1) {
                        fout.write(data, 0, count);
                    }
                    sender.sendMessage(plugin.tag + ChatColor.GREEN + "Finished downloading.");
                }
            }
        } catch (IOException e) {
            sender.sendMessage(plugin.tag + ChatColor.RED + "Could not download panel.");
            plugin.getLogger().log(Level.SEVERE,"",e);
        }
    }
}
