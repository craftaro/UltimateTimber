package com.songoda.ultimatetimber;

import com.songoda.ultimatetimber.commands.CommandHandler;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import com.songoda.ultimatetimber.treefall.TreeFallEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/*
Note: In this plugin, I have called the act of a tree falling over with pseudo-physics "toppling over". This is reflected
in the documentation, config files and variable names.
PS: MagmaGuy was here
 */

public class UltimateTimber extends JavaPlugin {

    public static Plugin plugin;
    public static List<World> validWorlds = new ArrayList<>();

    @Override
    public void onEnable() {

        plugin = this;
        /*
        Register the main event that handles toppling down trees
         */
        Bukkit.getServer().getPluginManager().registerEvents(new TreeFallEvent(), this);

        /*
        Initialize and cache config
         */
        DefaultConfig.initialize();

        /*
        Cache valid worlds for later use
         */
        for (World world : Bukkit.getWorlds())
            if (UltimateTimber.plugin.getConfig().getBoolean(DefaultConfig.VALID_WORLDS + world.getName()))
                validWorlds.add(world);

        this.getCommand("ultimatetimber").setExecutor(new CommandHandler());

    }

    @Override
    public void onDisable() {

        validWorlds.clear();

    }

}
