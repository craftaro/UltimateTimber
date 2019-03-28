package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;

abstract class Manager {

    protected UltimateTimber ultimateTimber;

    Manager(UltimateTimber ultimateTimber) {
        this.ultimateTimber = ultimateTimber;
    }

    /**
     * Reloads the Manager's settings
     */
    abstract void reload();

    /**
     * Cleans up the Manager's resources
     */
    abstract void disable();

}
