package me.rockyhawk.commandpanels.editor;

import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//todo
public class CommandPanelsEditorCommand implements CommandExecutor{
    final CommandPanels plugin;

    public CommandPanelsEditorCommand(CommandPanels pl) {
        this.plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("commandpanel.edit")){
            sender.sendMessage(plugin.tex.colour(plugin.getTag() + plugin.getDefaultConfig().getConfig().getString("config.format.perms")));
            return true;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(plugin.tex.colour( plugin.getTag() + ChatColor.RED + "Please execute command as a Player!"));
            return true;
        }
        Player p = (Player)sender;
        //below is going to go through the files and find the right one
        if (args.length == 1) { //check to make sure the person hasn't just left it empty
            for(Panel panel  : plugin.panelList){
                if(panel.getName().equals(args[0])) {
                    if(plugin.editorMain.settings.containsKey(p.getUniqueId())){
                        plugin.editorMain.settings.get(p.getUniqueId()).setLastPanel(panel.getName());
                    }else{
                        plugin.editorMain.settings.put(p.getUniqueId(), new EditorSettings("PanelEditMenu",panel.getName()));
                    }
                    //below will start the command, once it got the right file and panel
                    panel.copy().open(p,PanelPosition.TOP);
                    plugin.editorMain.openGuiPage(plugin.editorMain.settings.get(p.getUniqueId()).menuOpen,p,PanelPosition.MIDDLE);
                    plugin.editorMain.openGuiPage("BottomSettings",p,PanelPosition.BOTTOM);
                    return true;
                }
            }
        }
        sender.sendMessage(plugin.tex.colour(plugin.getTag() + ChatColor.RED + "Usage: /cpe <panel>"));
        return true;
    }
}
