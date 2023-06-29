package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;

public abstract class Manager {

    protected UltimateTimber plugin;

    Manager(UltimateTimber plugin) {
        this.plugin = plugin;
    }

    /**
     * Reloads the Manager's settings
     */
    public abstract void reload();

    /**
     * Cleans up the Manager's resources
     */
    public abstract void disable();

}
