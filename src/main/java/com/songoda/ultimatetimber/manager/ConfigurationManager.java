package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigurationManager extends Manager {
    public enum Setting {
        SERVER_TYPE(SettingType.STRING),
        LOCALE(SettingType.STRING),
        DISABLED_WORLDS(SettingType.STRING_LIST),
        MAX_LOGS_PER_CHOP(SettingType.INT),
        DESTROY_LEAVES(SettingType.BOOLEAN),
        LEAVES_REQUIRED_FOR_TREE(SettingType.INT),
        REALISTIC_TOOL_DAMAGE(SettingType.BOOLEAN),
        PROTECT_TOOL(SettingType.BOOLEAN),
        APPLY_SILK_TOUCH(SettingType.BOOLEAN),
        APPLY_SILK_TOUCH_TOOL_DAMAGE(SettingType.BOOLEAN),
        ALWAYS_REPLANT_SAPLING(SettingType.BOOLEAN),
        BREAK_ENTIRE_TREE_BASE(SettingType.BOOLEAN),
        DESTROY_INITIATED_BLOCK(SettingType.BOOLEAN),
        ONLY_DETECT_LOGS_UPWARDS(SettingType.BOOLEAN),
        ONLY_TOPPLE_WHILE(SettingType.STRING),
        ALLOW_CREATIVE_MODE(SettingType.BOOLEAN),
        REQUIRE_CHOP_PERMISSION(SettingType.BOOLEAN),
        PLAYER_TREE_TOPPLE_COOLDOWN(SettingType.BOOLEAN),
        PLAYER_TREE_TOPPLE_COOLDOWN_LENGTH(SettingType.INT),
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
        IGNORE_PLACED_BLOCKS(SettingType.BOOLEAN),
        IGNORE_PLACED_BLOCKS_MEMORY_SIZE(SettingType.INT),
        HOOKS_APPLY_EXPERIENCE(SettingType.BOOLEAN),
        HOOKS_APPLY_EXTRA_DROPS(SettingType.BOOLEAN),
        HOOKS_REQUIRE_ABILITY_ACTIVE(SettingType.BOOLEAN),
        TREE_ANIMATION_TYPE(SettingType.STRING),
        SCATTER_TREE_BLOCKS_ON_GROUND(SettingType.BOOLEAN),
        FRAGILE_BLOCKS(SettingType.STRING_LIST);

        private final SettingType settingType;
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
            return (boolean) this.value;
        }

        /**
         * Gets the setting as an int
         *
         * @return The setting as an int
         */
        public int getInt() {
            this.loadValue();
            return (int) this.value;
        }

        /**
         * Gets the setting as a double
         *
         * @return The setting a double
         */
        public double getDouble() {
            this.loadValue();
            return (double) this.value;
        }

        /**
         * Gets the setting as a String
         *
         * @return The setting a String
         */
        public String getString() {
            this.loadValue();
            return (String) this.value;
        }

        /**
         * Gets the setting as a string list
         *
         * @return The setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            this.loadValue();
            return (List<String>) this.value;
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
            if (this.value != null) {
                return;
            }

            FileConfiguration config = UltimateTimber.getPlugin(UltimateTimber.class).getConfigurationManager().getConfig();
            switch (this.settingType) {
                case BOOLEAN:
                    this.value = config.getBoolean(this.getNameAsKey());
                    break;
                case INT:
                    this.value = config.getInt(this.getNameAsKey());
                    break;
                case DOUBLE:
                    this.value = config.getDouble(this.getNameAsKey());
                    break;
                case STRING:
                    this.value = config.getString(this.getNameAsKey());
                    break;
                case STRING_LIST:
                    this.value = config.getStringList(this.getNameAsKey());
                    break;
            }
        }

        /**
         * Gets the name of this Setting as a FileConfiguration-compatible key
         *
         * @return The key for a FileConfiguration
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

    public ConfigurationManager(UltimateTimber plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        this.plugin.getCoreConfig().load();

        File configFile = new File(this.plugin.getDataFolder() + "/config.yml");

        // If an old config still exists, rename it, so it doesn't interfere
        if (configFile.exists() && this.plugin.getConfig().get("server-type") == null) {
            File renameConfigTo = new File(this.plugin.getDataFolder() + "/config-old.yml");
            configFile.renameTo(renameConfigTo);
            configFile = new File(this.plugin.getDataFolder() + "/config.yml");
        }

        // Create the new config if it doesn't exist
        if (!configFile.exists()) {
            String newConfigName = "config.yml";
            File newConfigFile = new File(this.plugin.getDataFolder() + "/" + newConfigName);
            this.plugin.saveResource(newConfigName, false);
            newConfigFile.renameTo(configFile);
        }

        this.configuration = YamlConfiguration.loadConfiguration(configFile);

        for (Setting setting : Setting.values()) {
            setting.reset();
        }
    }

    @Override
    public void disable() {
        for (Setting setting : Setting.values()) {
            setting.reset();
        }
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
