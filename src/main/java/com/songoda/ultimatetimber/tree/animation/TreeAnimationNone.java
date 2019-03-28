package com.songoda.ultimatetimber.tree.animation;

import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;

public class TreeAnimationNone extends TreeAnimation {

    public TreeAnimationNone(TreeBlockSet<TreeBlock> treeBlocks, TreeDefinition treeDefinition) {
        super(TreeAnimationType.NONE, treeBlocks, treeDefinition);
    }

    @Override
    public void playAnimation() {

    }

}
