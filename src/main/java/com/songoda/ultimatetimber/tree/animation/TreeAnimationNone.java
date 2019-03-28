package com.songoda.ultimatetimber.tree.animation;

import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;

public class TreeAnimationNone extends TreeAnimation {

    public TreeAnimationNone(TreeBlockSet<Block> treeBlocks, TreeDefinition treeDefinition) {
        super(TreeAnimationType.NONE, treeBlocks, treeDefinition);
    }

    @Override
    public void playAnimation() {

    }

}
