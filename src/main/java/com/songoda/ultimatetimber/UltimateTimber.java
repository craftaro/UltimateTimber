package com.songoda.ultimatetimber;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.songoda.ultimatetimber.commands.CommandHandler;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import com.songoda.ultimatetimber.hooks.JobsRebornHook;
import com.songoda.ultimatetimber.hooks.McMMOHook;
import com.songoda.ultimatetimber.treefall.CustomLoot;
import com.songoda.ultimatetimber.treefall.TreeFallAnimation;
import com.songoda.ultimatetimber.treefall.TreeFallListener;
import com.songoda.ultimatetimber.utils.Methods;

/*
Note: In this plugin, I have called the act of a tree falling over with pseudo-physics "toppling over". This is reflected
in the documentation, config files and variable names.
PS: MagmaGuy was here
 */

public class UltimateTimber extends JavaPlugin {
    
    private final static CommandSender console = Bukkit.getConsoleSender();
    private static UltimateTimber INSTANCE;
    
    private final String prefix = "&8[&6UltimateTimber&8]";
    private Set<String> validWorlds;
    private List<UUID> isNotChopping = new ArrayList<>();

    public static UltimateTimber getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {

        if (!checkVersion()) return;

        INSTANCE = this;

        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7" + this.getDescription().getName() + " " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));
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
        Check for McMMO
         */
        if (Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
            if (McMMOHook.setEnabled()) {
                console.sendMessage("Hooks: Hooked into mcMMO");
            } else {
                console.sendMessage("Hooks: Unable to hook with mcMMO, the version installed is not supported!");
            }
        }
        
        /*
        Check for Jobs Reborn
         */
        if (Bukkit.getPluginManager().isPluginEnabled("Jobs")) {
            if (JobsRebornHook.setEnabled()) {
                console.sendMessage("Hooks: Hooked into Jobs Reborn");
            } else {
                console.sendMessage("Hooks: Unable to hook with Jobs Reborn, the version installed is not supported!");
            }
        }
        
        /*
        Register command executor and tab completer
         */
        PluginCommand ultimatetimber = this.getCommand("ultimatetimber");
        CommandHandler commandHandler = new CommandHandler();
        ultimatetimber.setExecutor(commandHandler);
        ultimatetimber.setTabCompleter(commandHandler);

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

    private boolean checkVersion() {
        int workingVersion = 13;
        int currentVersion = Integer.parseInt(Bukkit.getServer().getClass()
                .getPackage().getName().split("\\.")[3].split("_")[1]);

        if (currentVersion < workingVersion) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                console.sendMessage("");
                console.sendMessage(String.format("%sYou installed the 1.%s only version of %s on a 1.%s server. Since you are on the wrong version we disabled the plugin for you. Please install correct version to continue using %s.", ChatColor.RED, workingVersion, this.getDescription().getName(), currentVersion, this.getDescription().getName()));
                console.sendMessage("");
            }, 20L);
            return false;
        }
        return true;
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

}
