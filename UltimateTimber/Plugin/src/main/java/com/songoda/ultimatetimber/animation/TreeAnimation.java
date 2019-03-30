package com.songoda.ultimatetimber.animation;

import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class TreeAnimation {

    protected final TreeAnimationType treeAnimationType;
    protected final TreeBlockSet<Block> treeBlocks;
    protected final TreeDefinition treeDefinition;

    TreeAnimation(TreeAnimationType treeAnimationType, TreeBlockSet<Block> treeBlocks, TreeDefinition treeDefinition) {
        this.treeAnimationType = treeAnimationType;
        this.treeBlocks = treeBlocks;
        this.treeDefinition = treeDefinition;
    }

    /**
     * Plays this tree topple animation
     *
     * @param detectedTree The DetectedTree
     * @param player The Player who toppled the tree
     */
    abstract void playAnimation(DetectedTree detectedTree, Player player);

}
