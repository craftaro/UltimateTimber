package com.songoda.epictimber;

import org.bukkit.configuration.Configuration;

public class DefaultConfig {

    /*
    This value is just cached so it can easily and safely be accessed during runtime
     */
    public static Configuration configuration;

    /*
    Storing these values in final strings makes it so you can change the keys or refactor their names later on without
    ever having to alter any code directly.
    Also they are easier to refer to using an IDE.
     */
    public static final String AXES_ONLY = "Only topple down trees cut down using axes";
    public static final String ACCURATE_AXE_DURABILITY = "Lower durability proportionately to the amount of blocks toppled down";
    public static final String CREATIVE_DISALLOWED = "Players in creative mode can't topple down trees";
    public static final String PERMISSIONS_ONLY = "Only allow players with the permission node to topple down trees";


    public static void initialize() {

        Configuration newConfiguration = EpicTimber.plugin.getConfig();

        newConfiguration.addDefault(AXES_ONLY, true);
        newConfiguration.addDefault(ACCURATE_AXE_DURABILITY, true);
        newConfiguration.addDefault(CREATIVE_DISALLOWED, true);
        newConfiguration.addDefault(PERMISSIONS_ONLY, true);

        newConfiguration.options().copyDefaults(true);

        EpicTimber.plugin.saveDefaultConfig();

        configuration = newConfiguration;

    }

}
