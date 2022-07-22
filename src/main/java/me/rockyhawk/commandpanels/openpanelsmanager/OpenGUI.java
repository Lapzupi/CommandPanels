package me.rockyhawk.commandpanels.openpanelsmanager;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.Panel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class OpenGUI {
    final CommandPanels plugin;

    public OpenGUI(CommandPanels pl) {
        this.plugin = pl;
    }

    public Inventory openGui(Panel panel, Player player, PanelPosition position, PanelOpenType openType, int animateValue) {
        ConfigurationSection pconfig = panel.getConfig();

        Inventory inventory;
        if (position == PanelPosition.TOP) {
            String title;
            if (openType != PanelOpenType.EDITOR) {
                //regular inventory
                title = plugin.tex.placeholders(panel, position, player, pconfig.getString("title"));
            } else {
                //editor inventory
                title = "Editing Panel: " + panel.getName();
            }

            if (isNumeric(pconfig.getString("rows"))) {
                inventory = Bukkit.createInventory(player, pconfig.getInt("rows") * 9, title);
            } else {
                inventory = Bukkit.createInventory(player, InventoryType.valueOf(pconfig.getString("rows")), title);
            }
        } else {
            inventory = player.getInventory();
            //if middle or bottom position, old items need to be cleared
            for (int c = 0; getInvSize(inventory, position) > c; ++c) {
                if (pconfig.getConfigurationSection("item").getKeys(false).contains(String.valueOf(c))) {
                    continue;
                }
                setItem(null, c, inventory, player, position);
            }
        }

        Set<String> itemList = pconfig.getConfigurationSection("item").getKeys(false);
        HashSet<Integer> takenSlots = new HashSet<>();
        for (String item : itemList) {
            String section = "";
            //openType needs to not be 3 so the editor won't include hasperm and hasvalue, etc items
            if (openType != PanelOpenType.EDITOR) {
                section = plugin.has.hasSection(panel, position, pconfig.getConfigurationSection("item." + Integer.parseInt(item)), player);
                //This section is for animations below here: VISUAL ONLY

                //check for if there is animations inside the items section
                if (pconfig.contains("item." + item + section + ".animate" + animateValue)) {
                    //check for if it contains the animate that has the animvatevalue
                    if (pconfig.contains("item." + item + section + ".animate" + animateValue)) {
                        section = section + ".animate" + animateValue;
                    }
                }
            }

            //will only add NBT if not an editor GUI
            ItemStack s = plugin.itemCreate.makeItemFromConfig(panel, position, Objects.requireNonNull(pconfig.getConfigurationSection("item." + item + section)), player, openType != PanelOpenType.EDITOR, openType != PanelOpenType.EDITOR, openType != PanelOpenType.EDITOR);

            //This is for CUSTOM ITEMS
            if (pconfig.contains("item." + item + section + ".itemType")) {
                //this is for contents in the itemType section
                if (pconfig.getStringList("item." + item + section + ".itemType").contains("placeable") && openType == PanelOpenType.REFRESH) {
                    //keep item the same, openType == 0 meaning panel is refreshing
                    setItem(player.getOpenInventory().getItem(Integer.parseInt(item)), Integer.parseInt(item), inventory, player, position);
                    takenSlots.add(Integer.parseInt(item));
                    continue;
                }
            }

            try {
                //place item into the GUI
                setItem(s, Integer.parseInt(item), inventory, player, position);
                takenSlots.add(Integer.parseInt(item));
                //i.setItem(Integer.parseInt(item), s);
                //only place duplicate items in without the editor mode. These are merely visual and will not carry over commands
                if (pconfig.contains("item." + item + section + ".duplicate") && openType != PanelOpenType.EDITOR) {
                    try {
                        String[] duplicateItems = pconfig.getString("item." + item + section + ".duplicate").split(",");
                        for (String tempDupe : duplicateItems) {
                            if (tempDupe.contains("-")) {
                                //if there is multiple dupe items, convert numbers to ints
                                int[] bothNumbers = new int[]{Integer.parseInt(tempDupe.split("-")[0]), Integer.parseInt(tempDupe.split("-")[1])};
                                for (int n = bothNumbers[0]; n <= bothNumbers[1]; n++) {
                                    try {
                                        if (!pconfig.contains("item." + n)) {
                                            setItem(s, n, inventory, player, position);
                                            takenSlots.add(n);
                                        }
                                    } catch (NullPointerException ignore) {
                                        setItem(s, n, inventory, player, position);
                                        takenSlots.add(n);
                                    }
                                }
                            } else {
                                //if there is only one dupe item
                                try {
                                    if (!pconfig.contains("item." + Integer.parseInt(tempDupe))) {
                                        setItem(s, Integer.parseInt(tempDupe), inventory, player, position);
                                        takenSlots.add(Integer.parseInt(tempDupe));
                                    }
                                } catch (NullPointerException ignore) {
                                    setItem(s, Integer.parseInt(tempDupe), inventory, player, position);
                                    takenSlots.add(Integer.parseInt(tempDupe));
                                }
                            }
                        }
                    } catch (NullPointerException nullp) {
                        plugin.debug(nullp, player);
                        player.closeInventory();
                        plugin.openPanels.closePanelForLoader(player.getName(), position);
                    }
                }
            } catch (ArrayIndexOutOfBoundsException ignore) {
            }
        }
        if (pconfig.contains("empty") && !Objects.equals(pconfig.getString("empty"), "AIR")) {
            ItemStack empty;
            try {
                //emptyID for older versions of minecraft (might be deprecated later on)
                short id = 0;
                if (pconfig.contains("emptyID")) {
                    id = Short.parseShort(pconfig.getString("emptyID"));
                }
                //either use custom item or just material type
                if (pconfig.contains("custom-item." + pconfig.getString("empty"))) {
                    empty = plugin.itemCreate.makeItemFromConfig(panel, position, pconfig.getConfigurationSection("custom-item." + pconfig.getString("empty")), player, true, true, true);
                } else {
                    empty = new ItemStack(Objects.requireNonNull(Material.matchMaterial(pconfig.getString("empty").toUpperCase())), 1, id);
                    empty = plugin.nbt.setNBT(empty);
                    ItemMeta renamedMeta = empty.getItemMeta();
                    assert renamedMeta != null;
                    renamedMeta.setDisplayName(" ");
                    empty.setItemMeta(renamedMeta);
                }
                if (empty.getType() != Material.AIR) {
                    for (int c = 0; getInvSize(inventory, position) > c; ++c) {
                        if (!takenSlots.contains(c)) {
                            //only place empty items if not editing
                            if (openType != PanelOpenType.EDITOR) {
                                setItem(empty, c, inventory, player, position);
                            }
                        }
                    }
                }
            } catch (IllegalArgumentException | NullPointerException var26) {
                plugin.debug(var26, player);
            }
        }
        if (openType == PanelOpenType.NORMAL) {
            //declare old panel closed
            if (plugin.openPanels.hasPanelOpen(player.getName(), position)) {
                plugin.openPanels.getOpenPanel(player.getName(), position).isOpen = false;
            }
            //open new panel
            plugin.openPanels.skipPanelClose.add(player.getName());
            plugin.openPanels.openPanelForLoader(player.getName(), panel, position);
            //only if it needs to open the top inventory
            if (position == PanelPosition.TOP) {
                player.openInventory(inventory);
            }
            plugin.openPanels.skipPanelClose.remove(player.getName());
        } else if (openType == PanelOpenType.EDITOR) {
            //The editor will always be at panel position top
            player.openInventory(inventory);
        } else if (openType == PanelOpenType.REFRESH) {
            //openType 0 will just refresh the panel
            if (position == PanelPosition.TOP) {
                player.getOpenInventory().getTopInventory().setContents(inventory.getStorageContents());
            }
        } else if (openType == PanelOpenType.RETURN) {
            //will return the inventory, not opening it at all
            return inventory;
        }
        return inventory;
    }

    private int getInvSize(Inventory inv, PanelPosition position) {
        if (position == PanelPosition.TOP) {
            return inv.getSize();
        } else if (position == PanelPosition.MIDDLE) {
            return 27;
        } else {
            return 9;
        }
    }

    private void setItem(ItemStack item, int slot, Inventory inv, Player p, PanelPosition position) throws ArrayIndexOutOfBoundsException {
        if (position == PanelPosition.TOP) {
            inv.setItem(slot, item);
        } else if (position == PanelPosition.MIDDLE) {
            if (slot + 9 < 36) {
                p.getInventory().setItem(slot + 9, item);
            }
        } else {
            if (slot < 9) {
                p.getInventory().setItem(slot, item);
            }
        }
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int unused = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
