package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;

import java.util.HashSet;
import java.util.Set;

public class DisabledWorldManager extends Manager {

    private Set<String> disabledWorldNames;

    public DisabledWorldManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        this.disabledWorldNames = new HashSet<>();
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

}