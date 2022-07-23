package me.rockyhawk.commandpanels.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.rockyhawk.commandpanels.CommandPanels;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.Response;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

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
                try {
                    downloadPanelAsync(url, fileName);
                    sender.sendMessage(plugin.tag + ChatColor.GREEN + "Finished downloading.");
                    plugin.reloadPanelFiles();
                } catch (IOException e) {
                    sender.sendMessage(plugin.tag + ChatColor.RED + "Could not download panel.");
                    plugin.getLogger().log(Level.SEVERE,"",e);
                }
            }
        }.runTask(plugin);
    }


    private void downloadPanelAsync(final String url,final String fileName) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(new File(plugin.panelsFolder, fileName + ".yml"))) {
            try (AsyncHttpClient client = Dsl.asyncHttpClient()) {

                client.prepareGet(url).execute(new AsyncCompletionHandler<FileOutputStream>() {
                    @Override
                    public State onBodyPartReceived(final HttpResponseBodyPart bodyPart) throws Exception {
                        stream.getChannel().write(bodyPart.getBodyByteBuffer());
                        return State.CONTINUE;
                    }

                    @Override
                    public FileOutputStream onCompleted(final Response response) throws Exception {
                        return stream;
                    }
                });
            }
        }
    }
}
