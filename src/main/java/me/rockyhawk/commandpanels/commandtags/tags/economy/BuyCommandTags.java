package me.rockyhawk.commandpanels.commandtags.tags.economy;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.commandtags.CommandTagEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class BuyCommandTags implements Listener {
    final CommandPanels plugin;

    public BuyCommandTags(CommandPanels pl) {
        this.plugin = pl;
    }

    @EventHandler
    public void commandTag(CommandTagEvent event) {
        if (!event.name.equalsIgnoreCase("buycommand=")) {
            return;
        }

        event.commandTagUsed();
        //if player uses buycommand [price] [command]
        try {
            if (plugin.econ == null) {
                plugin.tex.sendMessage(event.p, ChatColor.RED + "Buying Requires Vault and an Economy to work!");
                return;
            }

            if (plugin.econ.getBalance(event.p) >= Double.parseDouble(event.args[0])) {
                plugin.econ.withdrawPlayer(event.p, Double.parseDouble(event.args[0]));
                //execute command under here
                String price = event.args[0];
                String command = String.join(" ", Arrays.copyOfRange(event.raw, 1, event.raw.length));
                plugin.commandTags.runCommand(event.panel, event.pos, event.p, command);
                plugin.tex.sendMessage(event.p, plugin.config.getString("purchase.currency.success").replace("%cp-args%", price));
            } else {
                plugin.tex.sendMessage(event.p, plugin.config.getString("purchase.currency.failure"));
            }
        } catch (Exception buyc) {
            plugin.debug(buyc, event.p);
            plugin.tex.sendMessage(event.p, plugin.config.getString("config.format.error") + " " + "commands: " + event.name);
        }

    }
}
