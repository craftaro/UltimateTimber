package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigurationManager extends Manager {

    public enum Setting {
        SERVER_TYPE(SettingType.STRING),
        DISABLED_WORLDS(SettingType.STRING_LIST),
        MAX_LOGS_PER_CHOP(SettingType.INT),
        LEAVES_REQUIRED_FOR_TREE(SettingType.INT),
        REALISTIC_TOOL_DAMAGE(SettingType.BOOLEAN),
        PROTECT_TOOL(SettingType.BOOLEAN),
        BREAK_ENTIRE_TREE_BASE(SettingType.BOOLEAN),
        DESTROY_INITIATED_BLOCK(SettingType.BOOLEAN),
        ONLY_DETECT_LOGS_UPWARDS(SettingType.BOOLEAN),
        ONLY_TOPPLE_WHILE_SNEAKING(SettingType.BOOLEAN),
        ALLOW_CREATIVE_MODE(SettingType.BOOLEAN),
        REQUIRE_CHOP_PERMISSION(SettingType.BOOLEAN),
        IGNORE_REQUIRED_TOOLS(SettingType.BOOLEAN),
        REPLANT_SAPLINGS(SettingType.BOOLEAN),
        REPLANT_SAPLINGS_COOLDOWN(SettingType.INT),
        FALLING_BLOCKS_REPLANT_SAPLINGS(SettingType.BOOLEAN),
        FALLING_BLOCKS_REPLANT_SAPLINGS_CHANCE(SettingType.DOUBLE),
        FALLING_BLOCKS_DEAL_DAMAGE(SettingType.BOOLEAN),
        FALLING_BLOCK_DAMAGE(SettingType.INT),
        ADD_ITEMS_TO_INVENTORY(SettingType.BOOLEAN),
        USE_CUSTOM_SOUNDS(SettingType.BOOLEAN),
        USE_CUSTOM_PARTICLES(SettingType.BOOLEAN),
        BONUS_LOOT_MULTIPLIER(SettingType.DOUBLE),
        TREE_ANIMATION_TYPE(SettingType.STRING),
        SCATTER_TREE_BLOCKS_ON_GROUND(SettingType.BOOLEAN),
        MIX_ALL_TREE_TYPES(SettingType.BOOLEAN);

        private SettingType settingType;
        private Object value = null;

        Setting(SettingType settingType) {
            this.settingType = settingType;
        }

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
            if (this.value != null)
                return;

            switch (this.settingType) {
                case BOOLEAN:
                    this.value = UltimateTimber.getInstance().getConfigurationManager().getConfig().getBoolean(this.getNameAsKey());
                    break;
                case INT:
                    this.value = UltimateTimber.getInstance().getConfigurationManager().getConfig().getInt(this.getNameAsKey());
                    break;
                case DOUBLE:
                    this.value = UltimateTimber.getInstance().getConfigurationManager().getConfig().getDouble(this.getNameAsKey());
                    break;
                case STRING:
                    this.value = UltimateTimber.getInstance().getConfigurationManager().getConfig().getString(this.getNameAsKey());
                    break;
                case STRING_LIST:
                    this.value = UltimateTimber.getInstance().getConfigurationManager().getConfig().getStringList(this.getNameAsKey());
                    break;
            }
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

    private enum SettingType {
        BOOLEAN,
        INT,
        DOUBLE,
        STRING,
        STRING_LIST
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
