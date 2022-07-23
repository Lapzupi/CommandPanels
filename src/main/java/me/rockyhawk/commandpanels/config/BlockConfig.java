package me.rockyhawk.commandpanels.config;

import com.github.sarhatabaot.kraken.core.config.ConfigFile;
import me.rockyhawk.commandpanels.CommandPanels;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
public class BlockConfig extends ConfigFile<CommandPanels> {
    public BlockConfig(@NotNull final CommandPanels plugin) {
        super(plugin, "", "blocks.yml", "");
    }
}
