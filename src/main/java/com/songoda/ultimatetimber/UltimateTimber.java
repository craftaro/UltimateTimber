package com.songoda.ultimatetimber;

import com.songoda.ultimatetimber.commands.CommandHandler;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import com.songoda.ultimatetimber.treefall.CustomLoot;
import com.songoda.ultimatetimber.treefall.TreeFallAnimation;
import com.songoda.ultimatetimber.treefall.TreeFallEvent;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
Note: In this plugin, I have called the act of a tree falling over with pseudo-physics "toppling over". This is reflected
in the documentation, config files and variable names.
PS: MagmaGuy was here
 */

public class UltimateTimber extends JavaPlugin {
    private static CommandSender console = Bukkit.getConsoleSender();

    private final String prefix = "&8[&6UltimateTimber&8]";

    private static UltimateTimber INSTANCE;
    private List<World> validWorlds = new ArrayList<>();

    @Override
    public void onEnable() {
        INSTANCE = this;
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7" + this.getDescription().getName() + " " + this.getDescription().getVersion() + " by &5Brianna <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));
        /*
        Register the main event that handles toppling down trees
         */
        Bukkit.getServer().getPluginManager().registerEvents(new TreeFallEvent(), this);

        /*
        Prevent falling blocks from forming new blocks on the floor
         */
        Bukkit.getServer().getPluginManager().registerEvents(new TreeFallAnimation(), this);

        /*
        Initialize config
         */
        DefaultConfig.initialize();

        /*
        Initialize custom loot
         */
        CustomLoot.initializeCustomItems();

        /*
        Cache valid worlds for later use
         */
        for (World world : Bukkit.getWorlds())
            if (getConfig().getBoolean(DefaultConfig.VALID_WORLDS + world.getName()))
                validWorlds.add(world);

        this.getCommand("ultimatetimber").setExecutor(new CommandHandler(this));
        console.sendMessage(Methods.formatText("&a============================="));
    }

    @Override
    public void onDisable() {
        validWorlds.clear();
    }

    public static UltimateTimber getInstance() {
        return INSTANCE;
    }

    public List<World> getValidWorlds() {
        return Collections.unmodifiableList(validWorlds);
    }

    public String getPrefix() {
        return prefix;
    }
}
