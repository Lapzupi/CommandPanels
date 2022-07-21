package me.rockyhawk.commandpanels.classresources;


import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import me.rockyhawk.commandpanels.CommandPanels;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;
public class GetCustomHeads {
    final CommandPanels plugin;
    public GetCustomHeads(CommandPanels pl) {
        this.plugin = pl;
    }

    public String getHeadBase64(ItemStack head) {
        if(head.getType() == Material.PLAYER_HEAD) {
            NBTItem nbtItem = new NBTItem(head);
            NBTCompound skull = nbtItem.addCompound("SkullOwner");
            return skull.getCompound("Properties").getCompound("textures").getString("Value");
        }
        return null;
    }

    //getting the head from a Player
    public ItemStack getPlayerHead(String name) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack getCustomHead(String b64stringtexture) {
        //get head from base64
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1); // Creating the ItemStack, your input may vary.
        NBTItem nbti = new NBTItem(head); // Creating the wrapper.

        NBTCompound skull = nbti.addCompound("SkullOwner"); // Getting the compound, that way we can set the skin information
        skull.setString("Id", UUID.randomUUID().toString());
        // The UUID, note that skulls with the same UUID but different textures will misbehave and only one texture will load
        // (They'll share it), if skulls have different UUIDs and same textures they won't stack. See UUID.randomUUID();

        NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Value",  b64stringtexture);

        return nbti.getItem();
    }
}
