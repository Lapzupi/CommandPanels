package me.rockyhawk.commandpanels.ioclasses.legacy;

import org.bukkit.Bukkit;

public class LegacyVersion {
    public final MinecraftVersions LOCAL_VERSION;
    public LegacyVersion() {
        LOCAL_VERSION = MinecraftVersions.get(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
    }

}
