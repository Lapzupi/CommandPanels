package me.rockyhawk.commandpanels.commandtags.tags.standard;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.commandtags.CommandTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BungeeTags implements Listener {
    CommandPanels plugin;
    public BungeeTags(CommandPanels pl) {
        this.plugin = pl;
    }

    @EventHandler
    public void commandTag(CommandTagEvent e){
        if(e.name.equalsIgnoreCase("server=")){
            e.commandTagUsed();
            //this contacts bungee and tells it to send the server change command
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(e.args[0]);
            Player player = Bukkit.getPlayerExact(e.p.getName());
            assert player != null;
            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
    }
}
