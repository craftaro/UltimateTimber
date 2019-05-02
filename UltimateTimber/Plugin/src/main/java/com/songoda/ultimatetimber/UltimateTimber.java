package com.songoda.ultimatetimber;

import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.current.CurrentAdapter;
import com.songoda.ultimatetimber.adapter.legacy.LegacyAdapter;
import com.songoda.ultimatetimber.locale.LocaleModule;
import com.songoda.ultimatetimber.manager.ChoppingManager;
import com.songoda.ultimatetimber.manager.CommandManager;
import com.songoda.ultimatetimber.manager.ConfigurationManager;
import com.songoda.ultimatetimber.manager.HookManager;
import com.songoda.ultimatetimber.manager.LocaleManager;
import com.songoda.ultimatetimber.manager.Manager;
import com.songoda.ultimatetimber.manager.PlacedBlockManager;
import com.songoda.ultimatetimber.manager.SaplingManager;
import com.songoda.ultimatetimber.manager.TreeAnimationManager;
import com.songoda.ultimatetimber.manager.TreeDefinitionManager;
import com.songoda.ultimatetimber.manager.TreeDetectionManager;
import com.songoda.ultimatetimber.manager.TreeFallManager;
import com.songoda.ultimatetimber.utils.Methods;
import com.songoda.ultimatetimber.utils.Metrics;
import com.songoda.ultimatetimber.utils.NMSUtil;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Esophose
 */
public class UltimateTimber extends JavaPlugin {

    private static UltimateTimber INSTANCE;

    private final CommandSender console = Bukkit.getConsoleSender();

    private Set<Manager> managers;

    private VersionAdapter versionAdapter;
    private ChoppingManager choppingManager;
    private CommandManager commandManager;
    private ConfigurationManager configurationManager;
    private HookManager hookManager;
    private LocaleManager localeManager;
    private PlacedBlockManager placedBlockManager;
    private SaplingManager saplingManager;
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

        this.console.sendMessage(Methods.formatText("&a============================="));
        this.console.sendMessage(Methods.formatText("&7" + this.getDescription().getName() + " " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        this.console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

        // Songoda Updater
        Plugin plugin = new Plugin(this, 18);
        plugin.addModule(new LocaleModule());
        SongodaUpdate.load(plugin);

        // bStats Metrics
        new Metrics(this);

        // Register managers
        this.managers = new HashSet<>();
        this.choppingManager = this.registerManager(ChoppingManager.class);
        this.commandManager = this.registerManager(CommandManager.class);
        this.configurationManager = new ConfigurationManager(this);
        this.hookManager = this.registerManager(HookManager.class);
        this.localeManager = this.registerManager(LocaleManager.class);
        this.placedBlockManager = this.registerManager(PlacedBlockManager.class);
        this.saplingManager = this.registerManager(SaplingManager.class);
        this.treeAnimationManager = this.registerManager(TreeAnimationManager.class);
        this.treeDefinitionManager = this.registerManager(TreeDefinitionManager.class);
        this.treeDetectionManager = this.registerManager(TreeDetectionManager.class);
        this.treeFallManager = this.registerManager(TreeFallManager.class);

        // Load version adapter and managers
        this.setupVersionAdapter();
        this.reload();

        this.console.sendMessage(Methods.formatText("&a============================="));
    }

    @Override
    public void onDisable() {
        this.console.sendMessage(Methods.formatText("&a============================="));
        this.console.sendMessage(Methods.formatText("&7" + this.getDescription().getName() + " " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        this.console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        
        this.disable();

        this.console.sendMessage(Methods.formatText("&a============================="));
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
        return this.configurationManager;
    }

    /**
     * Gets the hook manager
     *
     * @return The HookManager instance
     */
    public HookManager getHookManager() {
        return this.hookManager;
    }

    /**
     * Gets the locale manager
     *
     * @return The LocaleManager instance
     */
    public LocaleManager getLocaleManager() {
        return this.localeManager;
    }

    /**
     * Gets the placed block manager
     *
     * @return The PlacedBlockManager instance
     */
    public PlacedBlockManager getPlacedBlockManager() {
        return this.placedBlockManager;
    }

    /**
     * Gets the sapling manager
     *
     * @return The SaplingManager instance
     */
    public SaplingManager getSaplingManager() {
        return this.saplingManager;
    }

    /**
     * Gets the tree animation manager
     *
     * @return The TreeAnimationManager instance
     */
    public TreeAnimationManager getTreeAnimationManager() {
        return this.treeAnimationManager;
    }

    /**
     * Gets the tree definition manager
     *
     * @return The TreeDefinitionManager instance
     */
    public TreeDefinitionManager getTreeDefinitionManager() {
        return this.treeDefinitionManager;
    }

    /**
     * Gets the tree detection manager
     *
     * @return The TreeDetectionManager instance
     */
    public TreeDetectionManager getTreeDetectionManager() {
        return this.treeDetectionManager;
    }

    /**
     * Gets the tree fall manager
     *
     * @return The TreeFallManager instance
     */
    public TreeFallManager getTreeFallManager() {
        return this.treeFallManager;
    }

}
