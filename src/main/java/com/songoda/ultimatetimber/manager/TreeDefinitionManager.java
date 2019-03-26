package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.tree.TreeDefinition;

import java.util.HashSet;
import java.util.Set;

public class TreeDefinitionManager {

    private UltimateTimber ultimateTimber;
    private Set<TreeDefinition> treeDefinitions;

    public TreeDefinitionManager(UltimateTimber ultimateTimber) {
        this.ultimateTimber = ultimateTimber;
        this.treeDefinitions = new HashSet<>();
    }

    public void load() {

    }

}
