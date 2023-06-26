package com.craftaro.ultimatetimber.animation.impl;

import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.ultimatetimber.animation.TreeAnimation;
import com.craftaro.ultimatetimber.tree.DetectedTree;
import com.craftaro.ultimatetimber.tree.ITreeBlock;
import com.craftaro.ultimatetimber.tree.TreeBlock;
import com.craftaro.ultimatetimber.utils.ParticleUtils;
import com.craftaro.ultimatetimber.utils.SoundUtils;
import com.craftaro.ultimatetimber.animation.TreeAnimationType;
import com.craftaro.ultimatetimber.managers.TreeDefinitionManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TreeAnimationNone extends TreeAnimation {

    public TreeAnimationNone(DetectedTree detectedTree, Player player) {
        super(TreeAnimationType.NONE, detectedTree, player);
    }

    @Override
    public void playAnimation(Runnable whenFinished) {
        UltimateTimber plugin = UltimateTimber.getInstance();
        TreeDefinitionManager treeDefinitionManager = UltimateTimber.getInstance().getTreeDefinitionManager();

        if (plugin.getMainConfig().getBoolean("Settings.Use Custom Sounds"))
            SoundUtils.playFallingSound(this.detectedTree.getDetectedTreeBlocks().getInitialLogBlock());

        if (plugin.getMainConfig().getBoolean("Settings.Use Custom Particles"))
            for (ITreeBlock<Block> treeBlock : this.detectedTree.getDetectedTreeBlocks().getAllTreeBlocks())
                ParticleUtils.playFallingParticles(treeBlock);

        for (ITreeBlock<Block> treeBlock : this.detectedTree.getDetectedTreeBlocks().getAllTreeBlocks()) {
            treeDefinitionManager.dropTreeLoot(this.detectedTree.getTreeDefinition(), treeBlock, this.player, this.hasSilkTouch, false);
            this.replaceBlock((TreeBlock) treeBlock);
        }

        whenFinished.run();
    }

}