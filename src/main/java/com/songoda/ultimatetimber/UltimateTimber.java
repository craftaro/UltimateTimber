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

public class UltimateTimber extends JavaPlugin {

    private static final CommandSender console = Bukkit.getConsoleSender();
    private static UltimateTimber INSTANCE;

    private VersionAdapter versionAdapter;
    private ConfigurationManager configurationManager;
    private DisabledWorldManager disabledWorldManager;
    private HookManager hookManager;
    private MessageManager messageManager;
    private SettingsManager settingsManager;
    private TreeAnimationManager treeAnimationManager;
    private TreeDefinitionManager treeDefinitionManager;
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

        this.configurationManager = new ConfigurationManager(this);
        this.disabledWorldManager = new DisabledWorldManager(this);
        this.hookManager = new HookManager(this);
        this.messageManager = new MessageManager(this);
        this.settingsManager = new SettingsManager(this);
        this.treeAnimationManager = new TreeAnimationManager(this);
        this.treeDefinitionManager = new TreeDefinitionManager(this);
        this.treeFallManager = new TreeFallManager(this);

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
        

        
        console.sendMessage(Methods.formatText("&a============================="));
    }

    /**
     * Reloads the plugin's settings
     */
    public void reload() {

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

    public VersionAdapter getVersionAdapter() {
        return this.versionAdapter;
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
     * Gets the disabled world manager
     *
     * @return The DisabledWorldManager instance
     */
    public DisabledWorldManager getDisabledWorldManager() {
        return disabledWorldManager;
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
     * Gets the configuration manager
     *
     * @return The ConfigurationManager instance
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * Gets the settings manager
     *
     * @return The SettingsManager instance
     */
    public SettingsManager getSettingsManager() {
        return settingsManager;
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
     * Gets the tree fall manager
     *
     * @return The TreeFallManager instance
     */
    public TreeFallManager getTreeFallManager() {
        return treeFallManager;
    }

}
