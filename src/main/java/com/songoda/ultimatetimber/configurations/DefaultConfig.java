package com.songoda.ultimatetimber.configurations;

import com.songoda.ultimatetimber.UltimateTimber;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;

import java.util.Collections;

public class DefaultConfig {

    /*
    This value is just cached so it can easily and safely be accessed during runtime
     */
//    public static Configuration configuration;

    /*
    Storing these values in final strings makes it so you can change the keys or refactor their names later on without
    ever having to alter any code directly.
    Also they are easier to refer to using an IDE.
     */
    public static final String AXES_ONLY = "Only topple down trees cut down using axes";
    public static final String ACCURATE_AXE_DURABILITY = "Lower durability proportionately to the amount of blocks toppled down";
    public static final String CREATIVE_DISALLOWED = "Players in creative mode can't topple down trees";
    public static final String PERMISSIONS_ONLY = "Only allow players with the permission node to topple down trees";
    public static final String VALID_WORLDS = "Valid worlds.";
    public static final String DAMAGE_PLAYERS = "Damage players when trees fall on them";
    public static final String REPLANT_SAPLING = "Replant sapling when tree is cut down";
    public static final String REPLANT_FROM_LEAVES = "Fallen leaves have a chance to plant saplings";
    public static final String CUSTOM_AUDIO = "Use custom sounds for trees falling";
    public static final String SHOW_ANIMATION = "Show tree fall animation";
    public static final String CUSTOM_LOOT_LIST = "Custom loot";
    private static final String CUSTOM_LOOT_ITEM = "Material:GOLDEN_APPLE,Chance:1";

    public static void initialize() {
        UltimateTimber plugin = UltimateTimber.getInstance();

        Configuration configuration = plugin.getConfig();

        configuration.addDefault(AXES_ONLY, true);
        configuration.addDefault(ACCURATE_AXE_DURABILITY, true);
        configuration.addDefault(CREATIVE_DISALLOWED, true);
        configuration.addDefault(PERMISSIONS_ONLY, true);
        configuration.addDefault(DAMAGE_PLAYERS, true);
        configuration.addDefault(REPLANT_SAPLING, true);
        configuration.addDefault(REPLANT_FROM_LEAVES, true);
        configuration.addDefault(CUSTOM_AUDIO, true);
        configuration.addDefault(SHOW_ANIMATION, true);

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
