package com.sheepion.dropname;

import com.sheepion.dropname.listener.AddNameToDrops;
import org.bukkit.plugin.java.JavaPlugin;

public final class DropName extends JavaPlugin {
    public static JavaPlugin plugin;

    public DropName() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new AddNameToDrops(), this);
    }

    @Override
    public void onDisable() {
    }
}
