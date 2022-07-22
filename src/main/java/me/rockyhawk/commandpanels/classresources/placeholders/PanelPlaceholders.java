package me.rockyhawk.commandpanels.classresources.placeholders;

import java.util.HashMap;
import java.util.Map;

public class PanelPlaceholders {
    public final Map<String,String> keys;

    public void addPlaceholder(String placeholder, String argument){
        keys.put(placeholder,argument);
    }

    public PanelPlaceholders(){
        keys = new HashMap<>();
    }
}
