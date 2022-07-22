package me.rockyhawk.commandpanels;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableMap;
import io.lumine.mythic.lib.api.item.NBTItem;
import me.rockyhawk.commandpanels.api.CommandPanelsAPI;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.classresources.ExecuteOpenVoids;
import me.rockyhawk.commandpanels.classresources.GetCustomHeads;
import me.rockyhawk.commandpanels.classresources.HasSections;
import me.rockyhawk.commandpanels.classresources.ItemCreation;
import me.rockyhawk.commandpanels.classresources.item_fall.ItemFallManager;
import me.rockyhawk.commandpanels.classresources.placeholders.CreateText;
import me.rockyhawk.commandpanels.classresources.placeholders.HexColours;
import me.rockyhawk.commandpanels.classresources.placeholders.Placeholders;
import me.rockyhawk.commandpanels.classresources.placeholders.expansion.CpPlaceholderExpansion;
import me.rockyhawk.commandpanels.commands.DataSubCommand;
import me.rockyhawk.commandpanels.commands.ImportSubCommand;
import me.rockyhawk.commandpanels.commands.MainCommand;
import me.rockyhawk.commandpanels.commands.PanelSubCommand;
import me.rockyhawk.commandpanels.commandtags.CommandTags;
import me.rockyhawk.commandpanels.customcommands.Commandpanelcustom;
import me.rockyhawk.commandpanels.datamanager.DebugManager;
import me.rockyhawk.commandpanels.datamanager.PanelDataLoader;
import me.rockyhawk.commandpanels.editor.CPEventHandler;
import me.rockyhawk.commandpanels.editor.CommandPanelsEditorCommand;
import me.rockyhawk.commandpanels.editor.CommandPanelsEditorMain;
import me.rockyhawk.commandpanels.editor.CommandPanelsEditorTabComplete;
import me.rockyhawk.commandpanels.generatepanels.GenUtils;
import me.rockyhawk.commandpanels.interactives.Commandpanelrefresher;
import me.rockyhawk.commandpanels.interactives.OpenOnJoin;
import me.rockyhawk.commandpanels.interactives.input.UserInputUtils;
import me.rockyhawk.commandpanels.ioclasses.NBTManager;
import me.rockyhawk.commandpanels.ioclasses.Sequence_1_13;
import me.rockyhawk.commandpanels.ioclasses.Sequence_1_14;
import me.rockyhawk.commandpanels.ioclasses.legacy.LegacyVersion;
import me.rockyhawk.commandpanels.ioclasses.legacy.MinecraftVersions;
import me.rockyhawk.commandpanels.ioclasses.legacy.PlayerHeads;
import me.rockyhawk.commandpanels.openpanelsmanager.OpenGUI;
import me.rockyhawk.commandpanels.openpanelsmanager.OpenPanelsLoader;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPermissions;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import me.rockyhawk.commandpanels.openpanelsmanager.UtilsPanelsLoader;
import me.rockyhawk.commandpanels.openwithitem.HotbarItemLoader;
import me.rockyhawk.commandpanels.openwithitem.SwapItemEvent;
import me.rockyhawk.commandpanels.openwithitem.UtilsChestSortEvent;
import me.rockyhawk.commandpanels.openwithitem.UtilsOpenWithItem;
import me.rockyhawk.commandpanels.panelblocks.BlocksTabComplete;
import me.rockyhawk.commandpanels.panelblocks.Commandpanelblocks;
import me.rockyhawk.commandpanels.panelblocks.PanelBlockOnClick;
import me.rockyhawk.commandpanels.playerinventoryhandler.InventorySaver;
import me.rockyhawk.commandpanels.playerinventoryhandler.ItemStackSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class CommandPanels extends JavaPlugin {
    public YamlConfiguration config;
    public Economy econ = null;
    public boolean openWithItem = false; //this will be true if there is a panel with open-with-item

    //initialise the tag
    public String tag = "[CommandPanels]";

    public final List<Player> generateMode = new ArrayList<>(); //players that are currently in generate mode
    public List<String[]> editorInputStrings = new ArrayList<>();
    public final List<Panel> panelList = new ArrayList<>(); //contains all the panels that are included in the panels folder
    private Utils utils;

    //get alternate classes
    public final CommandPanelsEditorMain editorMain = new CommandPanelsEditorMain(this);

    public final CommandTags commandTags = new CommandTags(this);
    public final PanelDataLoader panelData = new PanelDataLoader(this);
    public final Placeholders placeholders = new Placeholders(this);
    public final DebugManager debug = new DebugManager(this);
    public final CreateText tex = new CreateText(this);
    public final HexColours hex = new HexColours(this);

    public final ExecuteOpenVoids openVoids = new ExecuteOpenVoids(this);
    public final ItemCreation itemCreate = new ItemCreation(this);
    public final HasSections has = new HasSections(this);
    public final GetCustomHeads customHeads = new GetCustomHeads(this);
    public final PlayerHeads getHeads = new PlayerHeads(this);
    public final LegacyVersion legacy = new LegacyVersion(this);

    public final OpenPanelsLoader openPanels = new OpenPanelsLoader(this);
    public final OpenGUI createGUI = new OpenGUI(this);
    public final PanelPermissions panelPerms = new PanelPermissions(this);
    public final HotbarItemLoader hotbar = new HotbarItemLoader(this);
    public final NBTManager nbt = new NBTManager(this);

    public final InventorySaver inventorySaver = new InventorySaver(this);
    public final ItemStackSerializer itemSerializer = new ItemStackSerializer(this);
    public final UserInputUtils inputUtils = new UserInputUtils(this);

    public final File panelsFolder = new File(this.getDataFolder() + File.separator + "panels");
    public YamlConfiguration blockConfig; //where panel block locations are stored

    @Override
    public void onEnable() {
        getLogger().info("RockyHawk's CommandPanels v" + this.getDescription().getVersion() + " Plugin Loading...");

        //register config files
        this.blockConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "blocks.yml"));
        panelData.dataConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "data.yml"));
        inventorySaver.inventoryConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "inventories.yml"));
        this.config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder() + File.separator + "config.yml"));

        //save the config.yml file
        File configFile = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!configFile.exists()) {
            //generate a new config file from internal resources
            try {
                FileConfiguration configFileConfiguration = YamlConfiguration.loadConfiguration(getReaderFromStream(this.getResource("config.yml")));
                configFileConfiguration.save(configFile);
                this.config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder() + File.separator + "config.yml"));
            } catch (IOException var11) {
                getLogger().warning("WARNING: Could not save the config file!");
            }
        } else {
            //check if the config file has any missing elements
            try {
                YamlConfiguration configFileConfiguration = YamlConfiguration.loadConfiguration(getReaderFromStream(this.getResource("config.yml")));
                this.config.addDefaults(configFileConfiguration);
                this.config.options().copyDefaults(true);
                this.config.save(new File(this.getDataFolder() + File.separator + "config.yml"));
            } catch (IOException var10) {
                getLogger().warning("WARNING: Could not save the config file!");
            }
        }

        //setup class files
        this.setupEconomy();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Metrics metrics = new Metrics(this,5097);
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new MainCommand(this));
        paperCommandManager.registerCommand(new DataSubCommand(this));
        paperCommandManager.registerCommand(new ImportSubCommand(this));
        paperCommandManager.registerCommand(new PanelSubCommand(this));

        this.utils = new Utils(this);
        this.getServer().getPluginManager().registerEvents(utils, this);
        this.getServer().getPluginManager().registerEvents(inventorySaver, this);
        this.getServer().getPluginManager().registerEvents(inputUtils, this);
        this.getServer().getPluginManager().registerEvents(new UtilsPanelsLoader(this), this);
        this.getServer().getPluginManager().registerEvents(new GenUtils(this), this);
        this.getServer().getPluginManager().registerEvents(new ItemFallManager(this), this);
        this.getServer().getPluginManager().registerEvents(new OpenOnJoin(this), this);

        //load in PlaceholderAPI Expansion
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new CpPlaceholderExpansion(this).register();
        }

        //load in all built in command tags
        commandTags.registerBuiltInTags();

        //if refresh-panels set to false, don't load this
        if (Objects.requireNonNull(config.getString("config.refresh-panels")).equalsIgnoreCase("true")) {
            this.getServer().getPluginManager().registerEvents(new Commandpanelrefresher(this), this);
        }

        //if custom-commands set to false, don't load this
        if (Objects.requireNonNull(config.getString("config.custom-commands")).equalsIgnoreCase("true")) {
            this.getServer().getPluginManager().registerEvents(new Commandpanelcustom(this), this);
        }

        //if hotbar-items set to false, don't load this
        if (Objects.requireNonNull(config.getString("config.hotbar-items")).equalsIgnoreCase("true")) {
            this.getServer().getPluginManager().registerEvents(new UtilsOpenWithItem(this), this);
        }

        //if ingame-editor set to false, don't load this
        if (Objects.requireNonNull(config.getString("config.ingame-editor")).equalsIgnoreCase("true")) {
            this.getServer().getPluginManager().registerEvents(new CPEventHandler(this), this);
            Objects.requireNonNull(this.getCommand("commandpaneledit")).setTabCompleter(new CommandPanelsEditorTabComplete(this)); //todo
            Objects.requireNonNull(this.getCommand("commandpaneledit")).setExecutor(new CommandPanelsEditorCommand(this));
        }

        //if panel-blocks set to false, don't load this
        if (Objects.requireNonNull(config.getString("config.panel-blocks")).equalsIgnoreCase("true")) {
            Objects.requireNonNull(this.getCommand("commandpanelblock")).setExecutor(new Commandpanelblocks(this));
            Objects.requireNonNull(this.getCommand("commandpanelblock")).setTabCompleter(new BlocksTabComplete(this));
            this.getServer().getPluginManager().registerEvents(new PanelBlockOnClick(this), this);
        }

        //if 1.8 don't use this
        if (!Bukkit.getVersion().contains("1.8")) {
            this.getServer().getPluginManager().registerEvents(new SwapItemEvent(this), this);
        }

        //if plugin ChestSort is enabled
        if (getServer().getPluginManager().isPluginEnabled("ChestSort")) {
            this.getServer().getPluginManager().registerEvents(new UtilsChestSortEvent(this), this);
        }

        //save the example_top.yml file and the template.yml file
        if (!this.panelsFolder.exists()) {
            try {
                if (legacy.LOCAL_VERSION.lessThanOrEqualTo(MinecraftVersions.v1_12)) {
                    FileConfiguration exampleFileConfiguration = YamlConfiguration.loadConfiguration(getReaderFromStream(this.getResource("panels/exampleLegacy.yml")));
                    exampleFileConfiguration.save(new File(this.panelsFolder + File.separator + "example.yml"));
                } else {
                    //top
                    FileConfiguration exampleFileConfiguration = YamlConfiguration.loadConfiguration(getReaderFromStream(this.getResource("panels/example_top.yml")));
                    exampleFileConfiguration.save(new File(this.panelsFolder + File.separator + "panels/example_top.yml"));
                    //middle one
                    exampleFileConfiguration = YamlConfiguration.loadConfiguration(getReaderFromStream(this.getResource("panels/example_middle_one.yml")));
                    exampleFileConfiguration.save(new File(this.panelsFolder + File.separator + "panels/example_middle_one.yml"));
                    //middle two
                    exampleFileConfiguration = YamlConfiguration.loadConfiguration(getReaderFromStream(this.getResource("panels/example_middle_two.yml")));
                    exampleFileConfiguration.save(new File(this.panelsFolder + File.separator + "panels/example_middle_two.yml"));
                    //bottom
                    exampleFileConfiguration = YamlConfiguration.loadConfiguration(getReaderFromStream(this.getResource("panels/example_bottom.yml")));
                    exampleFileConfiguration.save(new File(this.panelsFolder + File.separator + "panels/example_bottom.yml"));
                }
                FileConfiguration templateFileConfiguration = YamlConfiguration.loadConfiguration(getReaderFromStream(this.getResource("panels/template.yml")));
                templateFileConfiguration.save(new File(this.panelsFolder + File.separator + "panels/template.yml"));
            } catch (IOException var11) {
                getLogger().warning("WARNING: Could not save the example file!");
            }
        }

        //load panelFiles
        reloadPanelFiles();

        //do hotbar items
        hotbar.reloadHotbarSlots();

        //add custom charts bStats
        //this is the total panels loaded
        metrics.addCustomChart(new SingleLineChart("panels_amount", panelList::size));

        //get tag
        tag = tex.colour(config.getString("config.format.tag"));

        getLogger().info("RockyHawk's CommandPanels v" + this.getDescription().getVersion() + " Plugin Loaded!");
    }

    @Override
    public void onDisable() {
        //close all the panels
        for (String name : openPanels.openPanels.keySet()) {
            openPanels.closePanelForLoader(name, PanelPosition.Top);
            try {
                Bukkit.getPlayer(name).closeInventory();
            } catch (Exception ignore) {
                //ignored
            }
        }

        //save files
        panelData.saveDataFile();
        inventorySaver.saveInventoryFile();
        Bukkit.getLogger().info("RockyHawk's CommandPanels Plugin Disabled, aww man.");
    }

    @Contract(" -> new")
    public static @NotNull CommandPanelsAPI getAPI() {
        return new CommandPanelsAPI(JavaPlugin.getPlugin(CommandPanels.class));
    }

    public ItemStack setName(Panel panel, ItemStack renamed, String customName, List<String> lore, Player p, Boolean usePlaceholders, Boolean useColours, Boolean hideAttributes) {
        try {
            ItemMeta renamedMeta = renamed.getItemMeta();
            //set cp placeholders
            if (usePlaceholders) {
                customName = tex.placeholdersNoColour(panel, PanelPosition.Top, p, customName);
            }
            if (useColours) {
                customName = tex.colour(customName);
            }

            assert renamedMeta != null;
            //hiding attributes will add an NBT tag
            if (hideAttributes) {
                renamedMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                renamedMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                renamedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                //HIDE_DYE was added into 1.17 api
                if (legacy.LOCAL_VERSION.greaterThanOrEqualTo(MinecraftVersions.v1_17)) {
                    renamedMeta.addItemFlags(ItemFlag.HIDE_DYE);
                }
            }
            if (customName != null) {
                renamedMeta.setDisplayName(customName);
            }

            List<String> re_lore;
            if (lore != null) {
                if (usePlaceholders && useColours) {
                    re_lore = tex.placeholdersList(panel, PanelPosition.Top, p, lore, true);
                } else if (usePlaceholders) {
                    re_lore = tex.placeholdersNoColour(panel, PanelPosition.Top, p, lore);
                } else if (useColours) {
                    re_lore = tex.placeholdersList(panel, PanelPosition.Top, p, lore, false);
                } else {
                    re_lore = lore;
                }
                renamedMeta.setLore(splitListWithEscape(re_lore));
            }
            renamed.setItemMeta(renamedMeta);
        } catch (Exception ignored) {
            //ignore
        }
        return renamed;
    }

    private void setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }

        this.econ = rsp.getProvider();
    }

    public boolean checkPanels(YamlConfiguration temp) {
        try {
            return temp.contains("panels");
        } catch (Exception var3) {
            return false;
        }
    }

    //check for duplicate panel names
    public boolean checkDuplicatePanel(CommandSender sender) {
        List<String> apanels = new ArrayList<>();
        for (Panel panel : panelList) {
            apanels.add(panel.getName());
        }

        //names is a list of the titles for the Panels
        Set<String> oset = new HashSet<>(apanels);
        if (oset.size() < apanels.size()) {
            //there are duplicate panel names
            ArrayList<String> opanelsTemp = new ArrayList<>();
            for (String tempName : apanels) {
                if (opanelsTemp.contains(tempName)) {
                    sender.sendMessage(tex.colour(tag) + ChatColor.RED + " Error duplicate panel name: " + tempName);
                    return false;
                }
                opanelsTemp.add(tempName);
            }
            return false;
        }
        return true;
    }

    //look through all files in all folders
    public void fileNamesFromDirectory(File directory) {
        for (String fileName : Objects.requireNonNull(directory.list())) {
            if (new File(directory + File.separator + fileName).isDirectory()) {
                fileNamesFromDirectory(new File(directory + File.separator + fileName));
                continue;
            }

            try {
                int ind = fileName.lastIndexOf(".");
                if (!fileName.substring(ind).equalsIgnoreCase(".yml") && !fileName.substring(ind).equalsIgnoreCase(".yaml")) {
                    continue;
                }
            } catch (Exception ex) {
                continue;
            }

            //check before adding the file to commandpanels
            if (!checkPanels(YamlConfiguration.loadConfiguration(new File(directory + File.separator + fileName)))) {
                this.getServer().getConsoleSender().sendMessage("[CommandPanels]" + ChatColor.RED + " Error in: " + fileName);
                continue;
            }
            for (String tempName : Objects.requireNonNull(YamlConfiguration.loadConfiguration(new File(directory + File.separator + fileName)).getConfigurationSection("panels")).getKeys(false)) {
                panelList.add(new Panel(new File((directory + File.separator + fileName)), tempName));
                if (YamlConfiguration.loadConfiguration(new File(directory + File.separator + fileName)).contains("panels." + tempName + ".open-with-item")) {
                    openWithItem = true;
                }
            }
        }
    }

    public void reloadPanelFiles() {
        panelList.clear();
        openWithItem = false;
        //load panel files
        fileNamesFromDirectory(panelsFolder);
    }

    public void debug(Exception e, Player p) {
        if (p == null) {
            if (debug.consoleDebug) {
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[CommandPanels] The plugin has generated a debug error, find the error below");
                e.printStackTrace();
            }
        } else {
            if (debug.isEnabled(p)) {
                p.sendMessage(tag + ChatColor.DARK_RED + "Check the console for a detailed error.");
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[CommandPanels] The plugin has generated a debug error, find the error below");
                e.printStackTrace();
            }
        }
    }

    public void helpMessage(CommandSender p) {
        p.sendMessage(tex.colour(tag + ChatColor.GREEN + "Commands:"));
        p.sendMessage(ChatColor.GOLD + "/cp <panel> [player:item] [player] " + ChatColor.WHITE + "Open a command panel.");
        if (p.hasPermission("commandpanel.reload")) {
            p.sendMessage(ChatColor.GOLD + "/cpr " + ChatColor.WHITE + "Reloads plugin config.");
        }
        if (p.hasPermission("commandpanel.generate")) {
            p.sendMessage(ChatColor.GOLD + "/cpg <rows> " + ChatColor.WHITE + "Generate GUI from popup menu.");
        }
        if (p.hasPermission("commandpanel.version")) {
            p.sendMessage(ChatColor.GOLD + "/cpv " + ChatColor.WHITE + "Display the current version.");
        }
        if (p.hasPermission("commandpanel.update")) {
            p.sendMessage(ChatColor.GOLD + "/cpv latest " + ChatColor.WHITE + "Download the latest update upon server reload/restart.");
            p.sendMessage(ChatColor.GOLD + "/cpv [version:cancel] " + ChatColor.WHITE + "Download an update upon server reload/restart.");
        }
        if (p.hasPermission("commandpanel.import")) {
            p.sendMessage(ChatColor.GOLD + "/cpi [file name] [URL] " + ChatColor.WHITE + "Downloads a panel from a raw link online.");
        }
        if (p.hasPermission("commandpanel.list")) {
            p.sendMessage(ChatColor.GOLD + "/cpl " + ChatColor.WHITE + "Lists the currently loaded panels.");
        }
        if (p.hasPermission("commandpanel.data")) {
            p.sendMessage(ChatColor.GOLD + "/cpdata " + ChatColor.WHITE + "Change panel data for a user.");
        }
        if (p.hasPermission("commandpanel.debug")) {
            p.sendMessage(ChatColor.GOLD + "/cpd " + ChatColor.WHITE + "Enable and Disable debug mode globally.");
        }
        if (p.hasPermission("commandpanel.block.add")) {
            p.sendMessage(ChatColor.GOLD + "/cpb add <panel> " + ChatColor.WHITE + "Add panel to a block being looked at.");
        }
        if (p.hasPermission("commandpanel.block.remove")) {
            p.sendMessage(ChatColor.GOLD + "/cpb remove " + ChatColor.WHITE + "Removes any panel assigned to a block looked at.");
        }
        if (p.hasPermission("commandpanel.block.list")) {
            p.sendMessage(ChatColor.GOLD + "/cpb list " + ChatColor.WHITE + "List blocks that will open panels.");
        }
        if (p.hasPermission("commandpanel.edit")) {
            p.sendMessage(ChatColor.GOLD + "/cpe <panel> " + ChatColor.WHITE + "Edit a panel with the Panel Editor.");
        }
    }

    public final Map<String, Color> colourCodes = ImmutableMap.of(
        "AQUA", Color.AQUA,
            "BLUE", Color.BLUE,
        "GRAY", Color.GRAY,
        "GREEN", Color.GREEN,
        "RED", Color.RED,
        "WHITE", Color.WHITE,
        "BLACK", Color.BLACK,
        "FUCHSIA", Color.FUCHSIA,
        "LIME", Color.LIME,
        "MAROON", Color.MAROON,
        "NAVY", Color.NAVY,
        "OLIVE", Color.OLIVE,
        "ORANGE", Color.ORANGE,
        "PURPLE", Color.PURPLE,
        "SILVER", Color.SILVER,
        "TEAL", Color.TEAL,
        "YELLOW", Color.YELLOW);

    public Reader getReaderFromStream(InputStream initialStream) throws IOException {
        //this reads the encrypted resource files in the jar file
        if (legacy.LOCAL_VERSION.lessThanOrEqualTo(MinecraftVersions.v1_13) || legacy.LOCAL_VERSION.greaterThanOrEqualTo(MinecraftVersions.v1_18)) {
            return new Sequence_1_13(this).getReaderFromStream(initialStream);
        } else {
            return new Sequence_1_14(this).getReaderFromStream(initialStream);
        }
    }

    //split lists using \n escape character
    public List<String> splitListWithEscape(List<String> list) {
        List<String> output = new ArrayList<>();
        for (String str : list) {
            output.addAll(Arrays.asList(str.split("\\\\n")));
        }
        return output;
    }

    public int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return utils.getRandom().nextInt((max - min) + 1) + min;
    }

    //returns true if the item is the MMO Item
    public boolean isMMOItem(ItemStack itm, String type, String id) {
        try {
            if (getServer().getPluginManager().isPluginEnabled("MMOItems")) {
                NBTItem nbtItem = NBTItem.get(itm);
                if (nbtItem.getType().equalsIgnoreCase(type) && nbtItem.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(id)) {
                    return true;
                }
                itm.getType();
            }
        } catch (Exception ex) {
            debug(ex, null);
        }
        return false;
    }

    public Utils getUtils() {
        return utils;
    }
}
