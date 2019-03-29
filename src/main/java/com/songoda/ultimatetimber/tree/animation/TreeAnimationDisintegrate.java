package com.songoda.ultimatetimber.tree.animation;

import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TreeAnimationDisintegrate extends TreeAnimation {

    public TreeAnimationDisintegrate(TreeBlockSet<Block> treeBlocks, TreeDefinition treeDefinition) {
        super(TreeAnimationType.DISINTIGRATE, treeBlocks, treeDefinition);
    }

    @Override
    public void playAnimation(DetectedTree detectedTree, Player player) {

    }

}
