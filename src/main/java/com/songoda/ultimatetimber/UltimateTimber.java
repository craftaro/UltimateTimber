package com.songoda.ultimatetimber;

import java.util.*;

import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.current.CurrentAdapter;
import com.songoda.ultimatetimber.adapter.legacy.LegacyAdapter;
import com.songoda.ultimatetimber.utils.Metrics;
import com.songoda.ultimatetimber.utils.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.songoda.ultimatetimber.commands.CommandHandler;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import com.songoda.ultimatetimber.manager.HookManager;
import com.songoda.ultimatetimber.treefall.CustomLoot;
import com.songoda.ultimatetimber.treefall.TreeFallAnimation;
import com.songoda.ultimatetimber.treefall.TreeFallListener;
import com.songoda.ultimatetimber.utils.Methods;

public class UltimateTimber extends JavaPlugin {

    private static final String prefix = "&8[&6UltimateTimber&8]";
    private static final CommandSender console = Bukkit.getConsoleSender();
    private static UltimateTimber INSTANCE;

    private VersionAdapter adapter;
    private Set<String> validWorlds = new HashSet<>();
    private List<UUID> isNotChopping = new ArrayList<>();

    public static UltimateTimber getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7" + this.getDescription().getName() + " " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

        /*
        Set up version adapter
         */
        this.setupAdapter();

        /*
        Register the main event that handles toppling down trees
         */
        Bukkit.getServer().getPluginManager().registerEvents(new TreeFallListener(), this);

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
        Cache valid world names for later use
         */
        this.reloadValidWorlds();
        
        /*
        Hook into supported plugins
         */
        HookManager.getInstance().hook();
        
        /*
        Register command executor and tab completer
         */
        PluginCommand ultimatetimber = this.getCommand("ultimatetimber");
        CommandHandler commandHandler = new CommandHandler();
        ultimatetimber.setExecutor(commandHandler);
        ultimatetimber.setTabCompleter(commandHandler);

        /*
        Set up metrics
         */
        new Metrics(this);
        
        console.sendMessage(Methods.formatText("&a============================="));
    }

    @Override
    public void onDisable() {
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7" + this.getDescription().getName() + " " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        
        this.validWorlds.clear();
        this.isNotChopping.clear();
        
        console.sendMessage(Methods.formatText("&a============================="));
    }
    
    public void reloadValidWorlds() {
        this.validWorlds = this.getConfig().getConfigurationSection(DefaultConfig.VALID_WORLDS).getKeys(false);
    }

    public boolean isWorldValid(World world) {
        return this.validWorlds.contains(world.getName());
    }

    public String getPrefix() {
        return prefix;
    }

    private void setupAdapter() {
        if (NMSUtil.getVersionNumber() > 12) {
            this.adapter = new CurrentAdapter();
        } else {
            this.adapter = new LegacyAdapter();
        }
    }

    public boolean toggleChopping(Player player) {
        boolean removed = this.isNotChopping.remove(player.getUniqueId());
        if (!removed)
            this.isNotChopping.add(player.getUniqueId());
        return removed;
    }

    public boolean isChopping(Player player) {
        return !this.isNotChopping.contains(player.getUniqueId());
    }

    public VersionAdapter getAdapter() {
        return this.adapter;
    }

}
