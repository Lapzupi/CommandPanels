package me.rockyhawk.commandpanels.interactives.input;

import me.rockyhawk.commandpanels.api.Panel;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class PlayerInput {
    public final Panel panel;
    public final ClickType click;
    public final List<String> commands;

    public PlayerInput(Panel panel1, List<String> commands1, ClickType click1){
        panel = panel1;
        click = click1;
        commands = commands1;
    }
}
