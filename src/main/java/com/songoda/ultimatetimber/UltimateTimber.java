package com.songoda.ultimatetimber;

import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.current.CurrentAdapter;
import com.songoda.ultimatetimber.adapter.legacy.LegacyAdapter;
import com.songoda.ultimatetimber.manager.*;
import com.songoda.ultimatetimber.utils.Methods;
import com.songoda.ultimatetimber.utils.Metrics;
import com.songoda.ultimatetimber.utils.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Esophose
 */
public class UltimateTimber extends JavaPlugin {

    private static final CommandSender console = Bukkit.getConsoleSender();
    private static UltimateTimber INSTANCE;

    private Set<Manager> managers;

    private VersionAdapter versionAdapter;
    private ChoppingManager choppingManager;
    private CommandManager commandManager;
    private ConfigurationManager configurationManager;
    private HookManager hookManager;
    private TreeAnimationManager treeAnimationManager;
    private TreeDefinitionManager treeDefinitionManager;
    private TreeDetectionManager treeDetectionManager;
    private TreeFallManager treeFallManager;

    public static UltimateTimber getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7" + this.getDescription().getName() + " " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

        this.managers = new HashSet<>();
        this.choppingManager = this.registerManager(ChoppingManager.class);
        this.commandManager = this.registerManager(CommandManager.class);
        this.configurationManager = new ConfigurationManager(this);
        this.hookManager = this.registerManager(HookManager.class);
        this.treeAnimationManager = this.registerManager(TreeAnimationManager.class);
        this.treeDefinitionManager = this.registerManager(TreeDefinitionManager.class);
        this.treeDetectionManager = this.registerManager(TreeDetectionManager.class);
        this.treeFallManager = this.registerManager(TreeFallManager.class);

        this.setupVersionAdapter();
        this.reload();

        new Metrics(this);
        
        console.sendMessage(Methods.formatText("&a============================="));
    }

    @Override
    public void onDisable() {
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7" + this.getDescription().getName() + " " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        
        this.disable();
        
        console.sendMessage(Methods.formatText("&a============================="));
    }

    /**
     * Reloads the plugin's settings
     */
    public void reload() {
        this.configurationManager.reload();
        this.managers.forEach(Manager::reload);
    }

    /**
     * Disables most of the plugin
     */
    public void disable() {
        this.configurationManager.disable();
        this.managers.forEach(Manager::disable);
    }

    /**
     * Sets up the version adapter
     */
    private void setupVersionAdapter() {
        if (NMSUtil.getVersionNumber() > 12) {
            this.versionAdapter = new CurrentAdapter();
        } else {
            this.versionAdapter = new LegacyAdapter();
        }
    }

    /**
     * Registers a manager
     *
     * @param managerClass The class of the manager to create a new instance of
     * @param <T> extends Manager
     * @return A new instance of the given manager class
     */
    private <T extends Manager> T registerManager(Class<T> managerClass) {
        try {
            T newManager = managerClass.getConstructor(UltimateTimber.class).newInstance(this);
            this.managers.add(newManager);
            return newManager;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the active version adapter being used
     *
     * @return The VersionAdapter being used for the plugin
     */
    public VersionAdapter getVersionAdapter() {
        return this.versionAdapter;
    }

    /**
     * Gets the chopping manager
     *
     * @return The ChoppingManager instance
     */
    public ChoppingManager getChoppingManager() {
        return this.choppingManager;
    }

    /**
     * Gets the command manager
     *
     * @return The CommandManager instance
     */
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    /**
     * Gets the configuration manager
     *
     * @return The ConfigurationManager instance
     */
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    /**
     * Gets the hook manager
     *
     * @return The HookManager instance
     */
    public HookManager getHookManager() {
        return hookManager;
    }

    /**
     * Gets the tree animation manager
     *
     * @return The TreeAnimationManager instance
     */
    public TreeAnimationManager getTreeAnimationManager() {
        return treeAnimationManager;
    }

    /**
     * Gets the tree definition manager
     *
     * @return The TreeDefinitionManager instance
     */
    public TreeDefinitionManager getTreeDefinitionManager() {
        return treeDefinitionManager;
    }

    /**
     * Gets the tree detection manager
     *
     * @return The TreeDetectionManager instance
     */
    public TreeDetectionManager getTreeDetectionManager() {
        return treeDetectionManager;
    }

    /**
     * Gets the tree fall manager
     *
     * @return The TreeFallManager instance
     */
    public TreeFallManager getTreeFallManager() {
        return treeFallManager;
    }

}
