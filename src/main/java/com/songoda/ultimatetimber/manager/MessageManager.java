package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;

public class MessageManager extends Manager {

    public enum MessageType {
        TOGGLE_ON,
        TOGGLE_OFF,
        RELOAD
    }

    private final String prefix = "&8[&6UltimateTimber&8]";

    public MessageManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    public String getPrefix() {
        return this.prefix;
    }

}
