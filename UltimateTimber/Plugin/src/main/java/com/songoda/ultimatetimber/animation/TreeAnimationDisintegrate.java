package com.songoda.ultimatetimber.animation;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.manager.ConfigurationManager;
import com.songoda.ultimatetimber.manager.TreeDefinitionManager;
import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TreeAnimationDisintegrate extends TreeAnimation {

    public TreeAnimationDisintegrate(DetectedTree detectedTree, Player player) {
        super(TreeAnimationType.DISINTEGRATE, detectedTree, player);
    }

    @Override
    public void playAnimation(Runnable whenFinished) {
        UltimateTimber ultimateTimber = UltimateTimber.getInstance();
        TreeDefinitionManager treeDefinitionManager = ultimateTimber.getTreeDefinitionManager();
        VersionAdapter versionAdapter = ultimateTimber.getVersionAdapter();

        boolean useCustomSound = ConfigurationManager.Setting.USE_CUSTOM_SOUNDS.getBoolean();
        boolean useCustomParticles = ConfigurationManager.Setting.USE_CUSTOM_PARTICLES.getBoolean();

        List<ITreeBlock<Block>> orderedLogBlocks = new ArrayList<>(this.detectedTree.getDetectedTreeBlocks().getLogBlocks());
        orderedLogBlocks.sort(Comparator.comparingInt(x -> x.getLocation().getBlockY()));

        List<ITreeBlock<Block>> leafBlocks = new ArrayList<>(this.detectedTree.getDetectedTreeBlocks().getLeafBlocks());
        Collections.shuffle(leafBlocks);

        Player p = this.player;
        TreeDefinition td = this.detectedTree.getTreeDefinition();
        boolean hst = this.hasSilkTouch;

        new BukkitRunnable() {
            @Override
            public void run() {
                List<ITreeBlock<Block>> toDestroy = new ArrayList<>();

                if (!orderedLogBlocks.isEmpty()) {
                    ITreeBlock<Block> treeBlock = orderedLogBlocks.remove(0);
                    toDestroy.add(treeBlock);
                } else if (!leafBlocks.isEmpty()) {
                    ITreeBlock<Block> treeBlock = leafBlocks.remove(0);
                    toDestroy.add(treeBlock);

                    if (!leafBlocks.isEmpty()) {
                        treeBlock = leafBlocks.remove(0);
                        toDestroy.add(treeBlock);
                    }
                }

                if (!toDestroy.isEmpty()) {
                    ITreeBlock<Block> first = toDestroy.get(0);
                    if (useCustomSound)
                        versionAdapter.playLandingSound(first);

                    for (ITreeBlock<Block> treeBlock : toDestroy) {
                        if (useCustomParticles)
                            versionAdapter.playFallingParticles(td, treeBlock);
                        treeDefinitionManager.dropTreeLoot(td, treeBlock, p, hst);
                        TreeAnimationDisintegrate.this.replaceBlock((TreeBlock)treeBlock);
                    }
                } else {
                    this.cancel();
                    whenFinished.run();
                }
            }
        }.runTaskTimer(ultimateTimber, 0, 1);
    }

}
