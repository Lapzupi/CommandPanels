package me.rockyhawk.commandpanels.ioclasses;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.rockyhawk.commandpanels.CommandPanels;
import org.bukkit.inventory.ItemStack;

public class NBTManager {
    public static final String COMMAND_PANELS = "CommandPanelsItem"; //?compound?
    final CommandPanels plugin;
    public NBTManager(CommandPanels pl) {
        this.plugin = pl;
    }

    //commandpanel item NBT
    public boolean hasNBT(ItemStack item){
        return new NBTItem(item).hasKey(COMMAND_PANELS);
    }

    public ItemStack setNBT(ItemStack item){
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(COMMAND_PANELS,"1");
        return nbtItem.getItem();
    }

    //custom key NBT
    public String getNBT(ItemStack item, String key){
        return new NBTItem(item).getString(key);
    }

    public ItemStack setNBT(ItemStack item, String key, String value){
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(key,value);
        return nbtItem.getItem();
    }
}
