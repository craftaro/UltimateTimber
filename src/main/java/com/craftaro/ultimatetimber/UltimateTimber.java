package com.craftaro.ultimatetimber;

import com.craftaro.core.CraftaroPlugin;
import com.craftaro.core.configuration.Config;
import com.craftaro.core.locale.LocaleManager;
import com.craftaro.ultimatetimber.managers.*;
import com.craftaro.ultimatetimber.commands.UltimateTimberCommands;
import com.craftaro.ultimatetimber.database.migrations._1_CreateTables;
import com.craftaro.ultimatetimber.listeners.ConnectionListeners;
import com.craftaro.ultimatetimber.players.PlayerManager;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Arrays;

public class UltimateTimber extends CraftaroPlugin {

    private static UltimateTimber INSTANCE;
    public static UltimateTimber getInstance() {
        return INSTANCE;
    }

    private Config mainConfig;
    private LocaleManager localeManager;
    private PlayerManager playerManager;
    private PlacedBlockManager placedBlockManager;
    private TreeDefinitionManager treeDefinitionManager;
    private SaplingManager saplingManager;
    private TreeAnimationManager treeAnimationManager;
    private TreeDetectionManager treeDetectionManager;
    private TreeFallManager treeFallManager;

    @Override
    public void onPluginEnable() {
        INSTANCE = this;
        initDatabase(Arrays.asList(new _1_CreateTables()));

        this.mainConfig = createUpdatingConfig(new File(getDataFolder(), "config.yml"));
        this.localeManager = new LocaleManager(this);
        this.playerManager = new PlayerManager(this);
        this.placedBlockManager = new PlacedBlockManager(this);
        this.treeDefinitionManager = new TreeDefinitionManager(this);
        this.saplingManager = new SaplingManager(this);
        this.treeAnimationManager = new TreeAnimationManager(this);
        this.treeDetectionManager = new TreeDetectionManager(this);
        this.treeFallManager = new TreeFallManager(this);

        Bukkit.getPluginManager().registerEvents(new ConnectionListeners(this), this);

        getCommandManager().register(new UltimateTimberCommands(this));
    }

    public void reloadPlugin() {
        this.mainConfig = createUpdatingConfig(new File(getDataFolder(), "config.yml"));
        this.localeManager = new LocaleManager(this);
        this.treeDetectionManager = new TreeDetectionManager(this);
    }

    @Override
    public void onPluginDisable() {
        playerManager.saveAllPlayers();
    }

    @Override
    protected int getPluginId() {
        return 0;
    }

    @Override
    protected String getPluginIcon() {
        return "OAK_SAPLING";
    }

    public Config getMainConfig() {
        return mainConfig;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public PlacedBlockManager getPlacedBlockManager() {
        return placedBlockManager;
    }

    public TreeDefinitionManager getTreeDefinitionManager() {
        return treeDefinitionManager;
    }

    public SaplingManager getSaplingManager() {
        return saplingManager;
    }

    public TreeAnimationManager getTreeAnimationManager() {
        return treeAnimationManager;
    }

    public TreeDetectionManager getTreeDetectionManager() {
        return treeDetectionManager;
    }
}
