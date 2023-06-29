package com.songoda.ultimatetimber;

import com.songoda.core.SongodaCore;
import com.songoda.core.SongodaPlugin;
import com.songoda.core.commands.CommandManager;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.hooks.LogManager;
import com.songoda.ultimatetimber.commands.CommandGiveAxe;
import com.songoda.ultimatetimber.commands.CommandReload;
import com.songoda.ultimatetimber.commands.CommandToggle;
import com.songoda.ultimatetimber.manager.ChoppingManager;
import com.songoda.ultimatetimber.manager.ConfigurationManager;
import com.songoda.ultimatetimber.manager.Manager;
import com.songoda.ultimatetimber.manager.PlacedBlockManager;
import com.songoda.ultimatetimber.manager.SaplingManager;
import com.songoda.ultimatetimber.manager.TreeAnimationManager;
import com.songoda.ultimatetimber.manager.TreeDefinitionManager;
import com.songoda.ultimatetimber.manager.TreeDetectionManager;
import com.songoda.ultimatetimber.manager.TreeFallManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UltimateTimber extends SongodaPlugin {
    private final Set<Manager> managers = new HashSet<>();

    private ChoppingManager choppingManager;
    private ConfigurationManager configurationManager;
    private PlacedBlockManager placedBlockManager;
    private SaplingManager saplingManager;
    private TreeAnimationManager treeAnimationManager;
    private TreeDefinitionManager treeDefinitionManager;
    private TreeDetectionManager treeDetectionManager;
    private TreeFallManager treeFallManager;

    /**
     * @deprecated Use {@link #getPlugin(Class)} instead
     */
    @Deprecated
    public static UltimateTimber getInstance() {
        return getPlugin(UltimateTimber.class);
    }

    @Override
    public void onPluginLoad() {
    }

    @Override
    public void onPluginEnable() {
        // Run Songoda Updater
        SongodaCore.registerPlugin(this, 18, CompatibleMaterial.IRON_AXE);

        // Load hooks
        LogManager.load();

        // Setup plugin commands
        CommandManager commandManager = new CommandManager(this);
        commandManager.addMainCommand("ut")
                .addSubCommands(
                        new CommandReload(this),
                        new CommandToggle(this),
                        new CommandGiveAxe(this)
                );

        // Register managers
        this.choppingManager = this.registerManager(ChoppingManager.class);
        this.configurationManager = new ConfigurationManager(this);
        this.placedBlockManager = this.registerManager(PlacedBlockManager.class);
        this.saplingManager = this.registerManager(SaplingManager.class);
        this.treeAnimationManager = this.registerManager(TreeAnimationManager.class);
        this.treeDefinitionManager = this.registerManager(TreeDefinitionManager.class);
        this.treeDetectionManager = this.registerManager(TreeDetectionManager.class);
        this.treeFallManager = this.registerManager(TreeFallManager.class);

        this.reloadConfig();
    }

    @Override
    public void onPluginDisable() {
        this.configurationManager.disable();
        this.managers.forEach(Manager::disable);
    }

    @Override
    public void onDataLoad() {
    }

    @Override
    public void onConfigReload() {
        this.configurationManager.reload();
        this.managers.forEach(Manager::reload);
        this.setLocale(getConfig().getString("locale"), true);
    }

    @Override
    public List<Config> getExtraConfig() {
        return Collections.emptyList();
    }

    /**
     * Registers a manager
     *
     * @param managerClass The class of the manager to create a new instance of
     * @param <T>          extends Manager
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

    public ChoppingManager getChoppingManager() {
        return this.choppingManager;
    }

    public ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    public PlacedBlockManager getPlacedBlockManager() {
        return this.placedBlockManager;
    }

    public SaplingManager getSaplingManager() {
        return this.saplingManager;
    }

    public TreeAnimationManager getTreeAnimationManager() {
        return this.treeAnimationManager;
    }

    public TreeDefinitionManager getTreeDefinitionManager() {
        return this.treeDefinitionManager;
    }

    public TreeDetectionManager getTreeDetectionManager() {
        return this.treeDetectionManager;
    }

    public TreeFallManager getTreeFallManager() {
        return this.treeFallManager;
    }
}
