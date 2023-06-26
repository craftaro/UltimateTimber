package com.craftaro.ultimatetimber.animation.impl;

import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.ultimatetimber.animation.TreeAnimation;
import com.craftaro.ultimatetimber.tree.*;
import com.craftaro.ultimatetimber.utils.ParticleUtils;
import com.craftaro.ultimatetimber.utils.SoundUtils;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatetimber.animation.TreeAnimationType;
import com.craftaro.ultimatetimber.managers.TreeDefinitionManager;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
        UltimateTimber plugin = UltimateTimber.getInstance();
        TreeDefinitionManager treeDefinitionManager = plugin.getTreeDefinitionManager();

        boolean useCustomSound = plugin.getMainConfig().getBoolean("Settings.Use Custom Sounds");
        boolean useCustomParticles = plugin.getMainConfig().getBoolean("Settings.Use Custom Particles");


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

                for (ITreeBlock<FallingBlock> fallingTreeBlock : TreeAnimationDisintegrate.this.fallingTreeBlocks.getAllTreeBlocks()) {
                    FallingBlock fallingBlock = fallingTreeBlock.getBlock();
                    fallingBlock.setVelocity(fallingBlock.getVelocity().clone().subtract(new Vector(0, 0.05, 0)));
                }

                if (!toDestroy.isEmpty()) {
                    ITreeBlock<Block> first = toDestroy.get(0);
                    if (useCustomSound)
                        SoundUtils.playLandingSound(first);

                    for (ITreeBlock<Block> treeBlock : toDestroy) {
                        if (treeBlock.getTreeBlockType().equals(TreeBlockType.LOG)) {
                            if (td.getLogMaterial().stream().noneMatch(x -> x.equals(XMaterial.matchXMaterial(treeBlock.getBlock().getType()))))
                                continue;
                        } else if (treeBlock.getTreeBlockType().equals(TreeBlockType.LEAF)) {
                            if (td.getLeafMaterial().stream().noneMatch(x -> x.equals(XMaterial.matchXMaterial(treeBlock.getBlock().getType()))))
                                continue;
                        }

                        if (useCustomParticles)
                            ParticleUtils.playFallingParticles(treeBlock);
                        treeDefinitionManager.dropTreeLoot(td, treeBlock, p, hst, false);
                        TreeAnimationDisintegrate.this.replaceBlock((TreeBlock) treeBlock);
                    }
                } else {
                    this.cancel();
                    whenFinished.run();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

}