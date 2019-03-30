package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigurationManager extends Manager {

    public enum Setting {
        SERVER_TYPE,
        DISABLED_WORLDS,
        MAX_LOGS_PER_CHOP,
        LEAVES_REQUIRED_FOR_TREE,
        REALISTIC_TOOL_DAMAGE,
        PROTECT_TOOL,
        BREAK_ENTIRE_TREE_BASE,
        DESTROY_INITIATED_BLOCK,
        ONLY_DETECT_LOGS_UPWARDS,
        ONLY_TOPPLE_WHILE_SNEAKING,
        ALLOW_CREATIVE_MODE,
        REQUIRE_CHOP_PERMISSION,
        IGNORE_REQUIRED_TOOLS,
        REPLANT_SAPLINGS,
        REPLANT_SAPLINGS_COOLDOWN,
        FALLING_BLOCKS_REPLANT_SAPLINGS,
        FALLING_BLOCKS_REPLANT_SAPLINGS_CHANCE,
        FALLING_BLOCKS_DEAL_DAMAGE,
        FALLING_BLOCK_DAMAGE,
        ADD_ITEMS_TO_INVENTORY,
        USE_CUSTOM_SOUNDS,
        USE_CUSTOM_PARTICLES,
        BONUS_LOOT_MULTIPLIER,
        TREE_ANIMATION_TYPE,
        SCATTER_TREE_BLOCKS_ON_GROUND,
        MIX_ALL_TREE_TYPES;

        private Object value = null;

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        public boolean getBoolean() {
            this.loadValue();
            return (boolean)this.value;
        }

        /**
         * Gets the setting as an int
         *
         * @return The setting as an int
         */
        public int getInt() {
            this.loadValue();
            return (int)this.value;
        }

        /**
         * Gets the setting as a double
         *
         * @return The setting a double
         */
        public double getDouble() {
            this.loadValue();
            return (double)this.value;
        }

        /**
         * Gets the setting as a String
         *
         * @return The setting a String
         */
        public String getString() {
            this.loadValue();
            return (String)this.value;
        }

        /**
         * Gets the setting as a string list
         *
         * @return The setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            this.loadValue();
            return (List<String>)this.value;
        }

        /**
         * Resets the cached value
         */
        public void reset() {
            this.value = null;
        }

        /**
         * Loads the value from the config and caches it if it isn't set yet
         */
        private void loadValue() {
            if (this.value == null)
                this.value = UltimateTimber.getInstance().getConfigurationManager().getConfig().get(this.getNameAsKey());
        }

        /**
         * Gets the name of this Setting as a config-compatible key
         *
         * @return The key in the config
         */
        private String getNameAsKey() {
            return this.name().replace("_", "-").toLowerCase();
        }
    }

    private YamlConfiguration configuration;

    public ConfigurationManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
    }

    @Override
    public void reload() {
        File configFile = new File(this.ultimateTimber.getDataFolder() + "/config.yml");

        // If an old config still exists, rename it so it doesn't interfere
        if (configFile.exists() && this.ultimateTimber.getConfig().get("server-type") == null) {
            File renameConfigTo = new File(this.ultimateTimber.getDataFolder() + "/config-old.yml");
            configFile.renameTo(renameConfigTo);
            configFile = new File(this.ultimateTimber.getDataFolder() + "/config.yml");
        }

        // Create the new config if it doesn't exist
        if (!configFile.exists()) {
            boolean isCurrentConfig = this.ultimateTimber.getVersionAdapter().getVersionAdapterType() == VersionAdapterType.CURRENT;
            String newConfigName = "config-" + (isCurrentConfig ? "current" : "legacy") + ".yml";
            File newConfigFile = new File(this.ultimateTimber.getDataFolder() + "/" + newConfigName);
            this.ultimateTimber.saveResource(newConfigName, false);
            newConfigFile.renameTo(configFile);
        }

        this.configuration = YamlConfiguration.loadConfiguration(configFile);

        for (Setting setting : Setting.values())
            setting.reset();
    }

    @Override
    public void disable() {
        for (Setting setting : Setting.values())
            setting.reset();
    }

    /**
     * Gets the config.yml as a YamlConfiguration
     *
     * @return The YamlConfiguration of the config.yml
     */
    public YamlConfiguration getConfig() {
        return this.configuration;
    }

}
