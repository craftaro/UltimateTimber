package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.animation.*;
import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.HashSet;
import java.util.Set;

public class TreeAnimationManager extends Manager implements Listener {

    private Set<TreeAnimation> activeAnimations;

    public TreeAnimationManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        this.activeAnimations = new HashSet<>();
        Bukkit.getPluginManager().registerEvents(this, ultimateTimber);
    }

    @Override
    public void reload() {
        this.activeAnimations.clear();
    }

    @Override
    public void disable() {
        this.activeAnimations.clear();
    }

    /**
     * Plays an animation for toppling a tree
     *
     * @param detectedTree The DetectedTree
     * @param player The Player who toppled the tree
     */
    public void runAnimation(DetectedTree detectedTree, Player player) {
        switch (ConfigurationManager.Setting.TREE_ANIMATION_TYPE.getString()) {
            case "FANCY":
                this.registerTreeAnimation(new TreeAnimationFancy(detectedTree, player));
                break;
            case "DISINTEGRATE":
                this.registerTreeAnimation(new TreeAnimationDisintegrate(detectedTree, player));
                break;
            case "CHAOS":
                this.registerTreeAnimation(new TreeAnimationChaos(detectedTree, player));
                break;
            case "NONE":
                this.registerTreeAnimation(new TreeAnimationNone(detectedTree, player));
                break;
        }
    }

    /**
     * Registers and runs a tree animation
     */
    private void registerTreeAnimation(TreeAnimation treeAnimation) {
        this.activeAnimations.add(treeAnimation);
        treeAnimation.playAnimation(() -> this.activeAnimations.remove(treeAnimation));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFallingBlockLand(EntityChangeBlockEvent event) {

    }

}
