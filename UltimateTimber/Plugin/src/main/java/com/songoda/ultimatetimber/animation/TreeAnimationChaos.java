package com.songoda.ultimatetimber.animation;

import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TreeAnimationChaos extends TreeAnimation {

    public TreeAnimationChaos(DetectedTree detectedTree, Player player) {
        super(TreeAnimationType.CHAOS, detectedTree, player);
    }

    @Override
    public void playAnimation(Runnable whenFinished) {

    }

}
