package me.rockyhawk.commandpanels.config;

import com.github.sarhatabaot.kraken.core.config.ConfigFile;
import me.rockyhawk.commandpanels.CommandPanels;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public class DefaultConfig extends ConfigFile<CommandPanels> {
    public DefaultConfig(@NotNull final CommandPanels plugin) {
        super(plugin, "", "config.yml", "");
    }
    //this.config.getConfig().options().copyDefaults(true); //todo add option to kraken

}
