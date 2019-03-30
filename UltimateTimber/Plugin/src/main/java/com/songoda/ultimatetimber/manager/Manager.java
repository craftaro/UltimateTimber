package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;

public abstract class Manager {

    protected UltimateTimber ultimateTimber;

    Manager(UltimateTimber ultimateTimber) {
        this.ultimateTimber = ultimateTimber;
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
