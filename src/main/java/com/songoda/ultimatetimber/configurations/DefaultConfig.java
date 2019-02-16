package com.songoda.ultimatetimber.configurations;

import com.songoda.ultimatetimber.UltimateTimber;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;

import java.util.Collections;

public class DefaultConfig {

    /*
    Storing these values in final strings makes it so you can change the keys or refactor their names later on without
    ever having to alter any code directly.
    Also they are easier to refer to using an IDE.
     */
    public static final String MAX_BRANCH_BLOCKS = "Max amount of logs that can be broken with one chop";
    public static final String LEAVES_FOR_TREE = "The number of leaves required to detect a valid tree";
    public static final String ONLY_BREAK_LOGS_UPWARDS = "Only break logs above the block broken";
    public static final String ALLOW_MIXED_TREE_TYPES = "Allow mixed log/leaf to be considered as one tree";
    public static final String AXES_ONLY = "Only topple down trees cut down using axes";
    public static final String TIMEOUT_BREAK = "Five second time out before you can break saplings";
    public static final String SNEAK_ONLY = "Only topple down trees cut down while sneaking";
    public static final String ACCURATE_AXE_DURABILITY = "Lower durability proportionately to the amount of blocks toppled down";
    public static final String CREATIVE_DISALLOWED = "Players in creative mode can't topple down trees";
    public static final String PERMISSIONS_ONLY = "Only allow players with the permission node to topple down trees";
    public static final String VALID_WORLDS = "Valid worlds.";
    public static final String DAMAGE_PLAYERS = "Damage players when trees fall on them";
    public static final String DELETE_BROKEN_LOG = "Delete the log that initiated the tree fall";
    public static final String REPLANT_SAPLING = "Replant sapling when tree is cut down";
    public static final String REPLANT_FROM_LEAVES = "Fallen leaves have a chance to plant saplings";
    public static final String CUSTOM_AUDIO = "Use custom sounds for trees falling";
    public static final String SHOW_ANIMATION = "Show tree fall animation";
    public static final String SCATTER_FALLEN_BLOCKS = "Scatter fallen tree blocks on the ground when animated";
    public static final String CUSTOM_LOOT_LIST = "Custom loot";
    public static final String CUSTOM_LOOT_ITEM = "Material:GOLDEN_APPLE,Chance:1";

    public static void initialize() {
        UltimateTimber plugin = UltimateTimber.getInstance();

        Configuration configuration = plugin.getConfig();

        configuration.addDefault(MAX_BRANCH_BLOCKS, 120);
        configuration.addDefault(LEAVES_FOR_TREE, 5);
        configuration.addDefault(ONLY_BREAK_LOGS_UPWARDS, true);
        configuration.addDefault(ALLOW_MIXED_TREE_TYPES, false);
        configuration.addDefault(AXES_ONLY, true);
        configuration.addDefault(TIMEOUT_BREAK, true);
        configuration.addDefault(SNEAK_ONLY, false);
        configuration.addDefault(ACCURATE_AXE_DURABILITY, true);
        configuration.addDefault(CREATIVE_DISALLOWED, true);
        configuration.addDefault(PERMISSIONS_ONLY, true);
        configuration.addDefault(DAMAGE_PLAYERS, true);
        configuration.addDefault(DELETE_BROKEN_LOG, false);
        configuration.addDefault(REPLANT_SAPLING, true);
        configuration.addDefault(REPLANT_FROM_LEAVES, true);
        configuration.addDefault(CUSTOM_AUDIO, true);
        configuration.addDefault(SHOW_ANIMATION, true);
        configuration.addDefault(SCATTER_FALLEN_BLOCKS, false);

        /*
        Add all worlds that exist in the world at startup
         */
        for (World world : Bukkit.getServer().getWorlds())
            configuration.addDefault(VALID_WORLDS + world.getName(), true);

        configuration.addDefault(CUSTOM_LOOT_LIST, Collections.singletonList(CUSTOM_LOOT_ITEM));

        configuration.options().copyDefaults(true);

        plugin.saveConfig();
        plugin.saveDefaultConfig();
    }

}
