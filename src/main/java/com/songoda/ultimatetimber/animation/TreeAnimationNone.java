package com.songoda.ultimatetimber.animation;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.manager.ConfigurationManager;
import com.songoda.ultimatetimber.manager.TreeDefinitionManager;
import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.utils.ParticleUtils;
import com.songoda.ultimatetimber.utils.SoundUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TreeAnimationNone extends TreeAnimation {

    public TreeAnimationNone(DetectedTree detectedTree, Player player) {
        super(TreeAnimationType.NONE, detectedTree, player);
    }

    @Override
    public void playAnimation(Runnable whenFinished) {
        TreeDefinitionManager treeDefinitionManager = UltimateTimber.getInstance().getTreeDefinitionManager();

        if (ConfigurationManager.Setting.USE_CUSTOM_SOUNDS.getBoolean())
            SoundUtils.playFallingSound(this.detectedTree.getDetectedTreeBlocks().getInitialLogBlock());

        if (ConfigurationManager.Setting.USE_CUSTOM_PARTICLES.getBoolean())
            for (ITreeBlock<Block> treeBlock : this.detectedTree.getDetectedTreeBlocks().getAllTreeBlocks())
                ParticleUtils.playFallingParticles(treeBlock);

        for (ITreeBlock<Block> treeBlock : this.detectedTree.getDetectedTreeBlocks().getAllTreeBlocks()) {
            treeDefinitionManager.dropTreeLoot(this.detectedTree.getTreeDefinition(), treeBlock, this.player, this.hasSilkTouch, false);
            this.replaceBlock((TreeBlock) treeBlock);
        }

        whenFinished.run();
    }

}
