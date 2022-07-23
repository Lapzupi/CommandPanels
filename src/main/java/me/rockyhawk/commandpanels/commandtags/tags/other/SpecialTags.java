package me.rockyhawk.commandpanels.commandtags.tags.other;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.commandtags.CommandTagEvent;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class SpecialTags implements Listener {
    final CommandPanels plugin;

    public SpecialTags(CommandPanels pl) {
        this.plugin = pl;
    }

    @EventHandler
    public void commandTag(CommandTagEvent event) {
        if (event.name.equalsIgnoreCase("open=")) {
            event.commandTagUsed();
            //if player uses open= it will open the panel, with the option to add custom placeholders
            String panelName = event.args[0];
            String cmd = String.join(" ", event.args).replace(event.args[0] + " ", "").trim();

            Panel openPanel = null;
            PanelPosition openPosition = event.pos;
            for (Panel pane : plugin.panelList) {
                if (pane.getName().equals(panelName)) {
                    openPanel = pane.copy();
                }
            }
            if (openPanel == null) {
                return;
            }

            Character[] cm = ArrayUtils.toObject(cmd.toCharArray());
            for (int i = 0; i < cm.length; i++) {
                if (cm[i].equals('[')) {
                    String contents = cmd.substring(i + 1, i + cmd.substring(i).indexOf(']'));
                    //do not change the placeholder
                    String placeholder = contents.substring(0, contents.indexOf(':'));
                    //only convert placeholders for the value
                    String value = plugin.tex.placeholders(event.panel, event.pos, event.p, contents.substring(contents.indexOf(':') + 1));
                    openPanel.placeholders.addPlaceholder(placeholder, value);
                    i = i + contents.length() - 1;
                } else if (cm[i].equals('{')) {
                    String contents = cmd.substring(i + 1, i + cmd.substring(i).indexOf('}'));
                    openPosition = PanelPosition.valueOf(contents);
                    i = i + contents.length() - 1;
                }
            }
            openPanel.open(event.p, openPosition);
            return;
        }
        if (event.name.equalsIgnoreCase("close=")) {
            event.commandTagUsed();
            //closes specific panel positions
            PanelPosition position = PanelPosition.valueOf(event.args[0]);
            if (position == PanelPosition.MIDDLE && plugin.openPanels.hasPanelOpen(event.p.getName(), position)) {
                plugin.openPanels.closePanelForLoader(event.p.getName(), PanelPosition.MIDDLE);
            } else if (position == PanelPosition.BOTTOM && plugin.openPanels.hasPanelOpen(event.p.getName(), position)) {
                plugin.openPanels.closePanelForLoader(event.p.getName(), PanelPosition.BOTTOM);
            } else if (position == PanelPosition.TOP && plugin.openPanels.hasPanelOpen(event.p.getName(), position)) {
                //closing top closes all
                plugin.commandTags.runCommand(event.panel, event.pos, event.p, "cpc");
            }
            return;
        }
        if (event.name.equalsIgnoreCase("title=")) {
            event.commandTagUsed();
            //added into the 1.11 API
            //will send a title to the player title= <player> <fadeIn> <stay> <fadeOut>
            if (event.args.length >= 5) {
                Player p = Bukkit.getPlayer(event.args[0]);
                StringBuilder message = new StringBuilder();
                for (int i = 4; i < event.args.length; i++) {
                    message.append(event.args[i]).append(" ");
                }
                message.deleteCharAt(message.length() - 1);
                String title;
                String subtitle = "";
                if (message.toString().contains("/n/")) {
                    title = plugin.tex.placeholders(event.panel, event.pos, event.p, message.toString().split("/n/")[0]);
                    subtitle = plugin.tex.placeholders(event.panel, event.pos, event.p, message.toString().split("/n/")[1]);
                } else {
                    title = plugin.tex.placeholders(event.panel, event.pos, event.p, message.toString().trim());
                }
                try {
                    p.sendTitle(title, subtitle, Integer.parseInt(event.args[1]), Integer.parseInt(event.args[2]), Integer.parseInt(event.args[3]));
                } catch (Exception ex) {
                    plugin.debug(ex, event.p);
                }
            }
            return;
        }
        if (event.name.equalsIgnoreCase("teleport=")) {
            event.commandTagUsed();
            if (event.args.length == 5) {
                float x;
                float y;
                float z;
                float yaw;
                float pitch; //pitch is the heads Y axis and yaw is the X axis
                x = Float.parseFloat(event.args[0]);
                y = Float.parseFloat(event.args[1]);
                z = Float.parseFloat(event.args[2]);
                yaw = Float.parseFloat(event.args[3]);
                pitch = Float.parseFloat(event.args[4]);
                event.p.teleport(new Location(event.p.getWorld(), x, y, z, yaw, pitch));
            } else if (event.args.length <= 3) {
                float x;
                float y;
                float z;
                x = Float.parseFloat(event.args[0]);
                y = Float.parseFloat(event.args[1]);
                z = Float.parseFloat(event.args[2]);
                event.p.teleport(new Location(event.p.getWorld(), x, y, z));
            } else {
                try {
                    Player otherplayer = Bukkit.getPlayer(event.args[3]);
                    float x;
                    float y;
                    float z;
                    x = Float.parseFloat(event.args[0]);
                    y = Float.parseFloat(event.args[1]);
                    z = Float.parseFloat(event.args[2]);
                    assert otherplayer != null;
                    otherplayer.teleport(new Location(otherplayer.getWorld(), x, y, z));
                } catch (Exception tpe) {
                    plugin.tex.sendMessage(event.p, plugin.getDefaultConfig().getConfig().getString("config.format.notitem"));
                }
            }
            return;
        }
        if (event.name.equalsIgnoreCase("delay=")) {
            event.commandTagUsed();
            //if player uses op= it will perform command as op
            final int delayTicks = Integer.parseInt(event.args[0]);
            String finalCommand = String.join(" ", event.args).replaceFirst(event.args[0], "").trim();
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        plugin.commandTags.runCommand(event.panel, event.pos, event.p, finalCommand);
                    } catch (Exception ex) {
                        //if there are any errors, cancel so that it doesn't loop errors
                        plugin.debug(ex, event.p);
                        this.cancel();
                    }
                    this.cancel();
                }
            }.runTaskTimer(plugin, delayTicks, 1); //20 ticks == 1 second
        }
    }
}
