package me.rockyhawk.commandpanels.ioclasses.legacy;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.ioclasses.GetStorageContents;
import me.rockyhawk.commandpanels.ioclasses.GetStorageContents_Legacy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class LegacyVersion {
    final CommandPanels plugin;
    public final MinecraftVersions LOCAL_VERSION;
    public LegacyVersion(CommandPanels pl) {
        this.plugin = pl;
        String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        LOCAL_VERSION = MinecraftVersions.get(VERSION);
    }

    public ItemStack[] getStorageContents(Inventory inventory){
        return inventory.getContents();
    }

    public void setStorageContents(Player player, ItemStack[] inventoryContents){
        player.getOpenInventory().getTopInventory().setContents(inventoryContents);
    }
}
