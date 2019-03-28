package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.tree.TreeDefinition;

import java.util.HashSet;
import java.util.Set;

public class TreeDefinitionManager extends Manager {

    private UltimateTimber ultimateTimber;
    private Set<TreeDefinition> treeDefinitions;

    public TreeDefinitionManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        this.treeDefinitions = new HashSet<>();
    }

    @Override
    public void reload() {
        this.treeDefinitions.clear();

        ConfigurationManager configurationManager = ultimateTimber.getConfigurationManager();
    }

    @Override
    public void disable() {
        this.treeDefinitions.clear();
    }

}
