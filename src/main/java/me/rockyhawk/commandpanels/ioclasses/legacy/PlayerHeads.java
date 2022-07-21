package me.rockyhawk.commandpanels.ioclasses.legacy;

import me.rockyhawk.commandpanels.CommandPanels;

@Deprecated
public class PlayerHeads {
    final CommandPanels plugin;
    public PlayerHeads(CommandPanels pl) {
        this.plugin = pl;
    }

    public boolean ifSkullOrHead(String material) {
        return material.equalsIgnoreCase("PLAYER_HEAD") || material.equalsIgnoreCase("SKULL_ITEM");
    }

}
