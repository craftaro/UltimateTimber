package com.songoda.ultimatetimber.tree.animation;

import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TreeAnimationFancy extends TreeAnimation {

    public TreeAnimationFancy(TreeBlockSet<Block> treeBlocks, TreeDefinition treeDefinition) {
        super(TreeAnimationType.FANCY, treeBlocks, treeDefinition);
    }

    @Override
    public void playAnimation(DetectedTree detectedTree, Player player) {

    }

}
