package me.rockyhawk.commandpanels.commandtags.tags.economy;


import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.commandtags.CommandTagEvent;
import me.rockyhawk.commandpanels.ioclasses.legacy.MinecraftVersions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BuyItemTags implements Listener {
    final CommandPanels plugin;

    public BuyItemTags(CommandPanels pl) {
        this.plugin = pl;
    }

    @EventHandler
    public void commandTag(CommandTagEvent e) {
        if (e.name.equalsIgnoreCase("buy=")) {
            e.commandTagUsed();
            //if player uses buy= it will be eg. buy= <price> <item> <amount of item> <ID>
            try {
                if (plugin.getEcon() != null) {
                    if (plugin.getEcon().getBalance(e.p) >= Double.parseDouble(e.args[0])) {
                        plugin.getEcon().withdrawPlayer(e.p, Double.parseDouble(e.args[0]));
                        plugin.tex.sendMessage(e.p, Objects.requireNonNull(plugin.getDefaultConfig().getConfig().getString("purchase.currency.success")).replaceAll("%cp-args%", e.args[0]));
                        giveItem(e.p, e.args);
                    } else {
                        plugin.tex.sendMessage(e.p, plugin.getDefaultConfig().getConfig().getString("purchase.currency.failure"));
                    }
                } else {
                    plugin.tex.sendMessage(e.p, ChatColor.RED + "Buying Requires Vault and an Economy to work!");
                }
            } catch (Exception buy) {
                plugin.debug(buy, e.p);
                plugin.tex.sendMessage(e.p, plugin.getDefaultConfig().getConfig().getString("config.format.error") + " " + "commands: " + e.name);
            }
        }
    }

    private void giveItem(Player p, String[] args) {
        //legacy ID
        byte id = 0;
        if (plugin.legacy.LOCAL_VERSION.lessThanOrEqualTo(MinecraftVersions.v1_15)) {
            for (String argsTemp : args) {
                if (argsTemp.startsWith("id:")) {
                    id = Byte.parseByte(argsTemp.replace("id:", ""));
                    break;
                }
            }
        }
        plugin.inventorySaver.addItem(p, new ItemStack(Objects.requireNonNull(Material.matchMaterial(args[1])), Integer.parseInt(args[2]), id));
    }
}
