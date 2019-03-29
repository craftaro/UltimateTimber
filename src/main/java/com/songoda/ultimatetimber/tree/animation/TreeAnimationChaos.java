package com.songoda.ultimatetimber.tree.animation;

import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TreeAnimationChaos extends TreeAnimation {

    public TreeAnimationChaos(TreeBlockSet<Block> treeBlocks, TreeDefinition treeDefinition) {
        super(TreeAnimationType.CHAOS, treeBlocks, treeDefinition);
    }

    @Override
    public void playAnimation(DetectedTree detectedTree, Player player) {

    }

}
