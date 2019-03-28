package com.songoda.ultimatetimber.tree.animation;

import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;

public abstract class TreeAnimation {

    protected final TreeAnimationType treeAnimationType;
    protected final TreeBlockSet<Block> treeBlocks;
    protected final TreeDefinition treeDefinition;

    TreeAnimation(TreeAnimationType treeAnimationType, TreeBlockSet<Block> treeBlocks, TreeDefinition treeDefinition) {
        this.treeAnimationType = treeAnimationType;
        this.treeBlocks = treeBlocks;
        this.treeDefinition = treeDefinition;
    }

    abstract void playAnimation();

}
