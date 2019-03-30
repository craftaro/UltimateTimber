package com.songoda.ultimatetimber.animation;

import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TreeAnimationNone extends TreeAnimation {

    public TreeAnimationNone(TreeBlockSet<Block> treeBlocks, TreeDefinition treeDefinition) {
        super(TreeAnimationType.NONE, treeBlocks, treeDefinition);
    }

    @Override
    public void playAnimation(DetectedTree detectedTree, Player player) {

    }

}
