package me.rockyhawk.commandpanels.openwithitem;

import me.rockyhawk.commandpanels.api.Panel;

import java.util.HashMap;
import java.util.Map;

public class HotbarPlayerManager {
    public final Map<Integer, Panel> list = new HashMap<>();

    public void addSlot(int slot, Panel panel) {
        list.put(slot, panel);
    }

    public Panel getPanel(int slot) {
        return list.get(slot).copy();
    }
}
