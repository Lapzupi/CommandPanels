package me.rockyhawk.commandpanels.interactives;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.api.PanelOpenedEvent;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelOpenType;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Commandpanelrefresher implements Listener {
    final CommandPanels plugin;

    public Commandpanelrefresher(CommandPanels pl) {
        this.plugin = pl;
    }

    @EventHandler
    public void onPanelOpen(PanelOpenedEvent event) { //Handles when Players open inventory
        if (plugin.config.contains("config.refresh-panels")) {
            if (Objects.requireNonNull(plugin.config.getString("config.refresh-panels")).trim().equalsIgnoreCase("false")) {
                return;
            }
        }

        final Player player = event.getPlayer();
        Panel panel = event.getPanel();


        //if panel has custom refresh delay
        int tempRefreshDelay = plugin.config.getInt("config.refresh-delay");
        if (panel.getConfig().contains("refresh-delay")) {
            tempRefreshDelay = panel.getConfig().getInt("refresh-delay");
        }

        final int refreshDelay = tempRefreshDelay;

        if (panel.getConfig().contains("panelType")) {
            if (panel.getConfig().getStringList("panelType").contains("static")) {
                //do not update temporary panels, only default panels
                return;
            }
        }

        new BukkitRunnable() {
            int c = 0;
            int animateCount = 0;

            @Override
            public void run() {
                int animatevalue = -1;
                if (panel.getConfig().contains("animatevalue")) {
                    animatevalue = panel.getConfig().getInt("animatevalue");
                }
                //counter counts to refresh delay (in seconds) then restarts
                if (c < refreshDelay) {
                    c += 1;
                } else {
                    c = 0;
                }
                //refresh here
                if (event.getPanel().isOpen) {
                    if (player.getOpenInventory().getTopInventory().getHolder() != player) {
                        //if open inventory is not a panel (owned by the player holder), cancel
                        this.cancel();
                        return;
                    }

                    if (c == 0) {
                        //animation counter
                        if (animatevalue != -1) {
                            if (animateCount < animatevalue) {
                                animateCount += 1;
                            } else {
                                animateCount = 0;
                            }
                        }
                        try {
                            if (plugin.debug.isEnabled(player) && panel.getFile() != null) {
                                //reload the panel is debug is enabled (only personal debug)
                                panel.setConfig(YamlConfiguration.loadConfiguration(panel.getFile()));
                            }
                            plugin.createGUI.openGui(panel, player, event.getPosition(), PanelOpenType.REFRESH, animateCount);
                        } catch (Exception ex) {
                            //error opening gui
                            player.closeInventory();
                            plugin.openPanels.closePanelForLoader(player.getName(), event.getPosition());
                            this.cancel();
                        }
                    }
                } else {
                    if (Objects.requireNonNull(plugin.config.getString("config.stop-sound")).trim().equalsIgnoreCase("true")) {
                        try {
                            player.stopSound(Sound.valueOf(Objects.requireNonNull(panel.getConfig().getString("sound-on-open")).toUpperCase()));
                        } catch (Exception sou) {
                            //skip
                        }
                    }
                    c = 0;
                    this.cancel();
                    //remove duplicate items here
                    player.updateInventory();
                    if (plugin.inventorySaver.hasNormalInventory(player)) {
                        for(ItemStack item : Arrays.stream(player.getInventory().getContents())
                                .filter(Objects::nonNull)
                                .filter(plugin.nbt::hasNBT).collect(Collectors.toList())) {
                            player.getInventory().remove(item);
                        }

                    }
                }
            }
        }.runTaskTimer(this.plugin, 1, 1); //20 ticks == 1 second (5 ticks = 0.25 of a second)
    }
}
