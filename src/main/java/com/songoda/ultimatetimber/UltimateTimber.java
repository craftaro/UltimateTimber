package com.songoda.ultimatetimber;

import com.songoda.ultimatetimber.commands.CommandHandler;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import com.songoda.ultimatetimber.treefall.CustomLoot;
import com.songoda.ultimatetimber.treefall.TreeFallAnimation;
import com.songoda.ultimatetimber.treefall.TreeFallListener;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/*
Note: In this plugin, I have called the act of a tree falling over with pseudo-physics "toppling over". This is reflected
in the documentation, config files and variable names.
PS: MagmaGuy was here
 */

public class UltimateTimber extends JavaPlugin {
    private static CommandSender console = Bukkit.getConsoleSender();
    private static UltimateTimber INSTANCE;
    private final String prefix = "&8[&6UltimateTimber&8]";
    private List<World> validWorlds = new ArrayList<>();
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

    public List<World> getValidWorlds() {
        return Collections.unmodifiableList(validWorlds);
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
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(String.format("%sYou installed the 1.%s only version of %s on a 1.%s server. Since you are on the wrong version we disabled the plugin for you. Please install correct version to continue using %s.", ChatColor.RED, workingVersion, this.getDescription().getName(), currentVersion, this.getDescription().getName()));
                Bukkit.getConsoleSender().sendMessage("");
            }, 20L);
            return false;
        }
        return true;
    }

    public boolean toggleChopping(Player player) {
        if (!isNotChopping.contains(player.getUniqueId())) {
            isNotChopping.add(player.getUniqueId());
            return false;
        }
        isNotChopping.remove(player.getUniqueId());
        return true;
    }

    public boolean isChopping(Player player) {
        return !isNotChopping.contains(player.getUniqueId());
    }


}
