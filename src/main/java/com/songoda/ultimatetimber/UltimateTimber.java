package com.songoda.ultimatetimber;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/*
Note: In this plugin, I have called the act of a tree falling over with pseudo-physics "toppling over". This is reflected
in the documentation, config files and variable names.
PS: MagmaGuy was here
 */

public class UltimateTimber extends JavaPlugin {

    public static Plugin plugin;

    @Override
    public void onEnable() {

        plugin = this;
        /*
        Register the main event that handles toppling down trees
         */
        Bukkit.getServer().getPluginManager().registerEvents(new TreeFallHandler(), this);

        /*
        Initialize and cache config
         */
        DefaultConfig.initialize();

    }

    @Override
    public void onDisable() {

    }

}
