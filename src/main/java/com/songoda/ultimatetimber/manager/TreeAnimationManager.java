package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class TreeAnimationManager extends Manager implements Listener {

    public TreeAnimationManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        Bukkit.getPluginManager().registerEvents(this, ultimateTimber);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    /**
     * Plays an animation for toppling a tree
     *
     * @param detectedTree The DetectedTree
     * @param player The Player who toppled the tree
     */
    public void runAnimation(DetectedTree detectedTree, Player player) {
        TreeDefinition treeDefinition = detectedTree.getTreeDefinition();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFallingBlockLand(EntityChangeBlockEvent event) {

    }

}
