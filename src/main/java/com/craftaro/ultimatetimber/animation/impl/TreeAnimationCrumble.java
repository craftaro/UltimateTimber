package com.craftaro.ultimatetimber.animation.impl;

import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.ultimatetimber.animation.TreeAnimation;
import com.craftaro.ultimatetimber.animation.TreeAnimationType;
import com.craftaro.ultimatetimber.tree.*;
import com.craftaro.ultimatetimber.utils.BlockUtils;
import com.craftaro.ultimatetimber.utils.ParticleUtils;
import com.craftaro.ultimatetimber.utils.SoundUtils;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TreeAnimationCrumble extends TreeAnimation {

    public TreeAnimationCrumble(DetectedTree detectedTree, Player player) {
        super(TreeAnimationType.CRUMBLE, detectedTree, player);
    }

    @Override
    public void playAnimation(Runnable whenFinished) {
        UltimateTimber plugin = UltimateTimber.getInstance();

        boolean useCustomSound = plugin.getMainConfig().getBoolean("Settings.Use Custom Sounds");
        boolean useCustomParticles = plugin.getMainConfig().getBoolean("Settings.Use Custom Particles");

        // Order blocks by y-axis, lowest first, but shuffled randomly
        int currentY = -1;
        List<List<ITreeBlock<Block>>> treeBlocks = new ArrayList<>();
        List<ITreeBlock<Block>> currentPartition = new ArrayList<>();
        List<ITreeBlock<Block>> orderedDetectedTreeBlocks = new ArrayList<>(this.detectedTree.getDetectedTreeBlocks().getAllTreeBlocks());
        orderedDetectedTreeBlocks.sort(Comparator.comparingInt(x -> x.getLocation().getBlockY()));
        for (ITreeBlock<Block> treeBlock : orderedDetectedTreeBlocks) {
            if (currentY != treeBlock.getLocation().getBlockY()) {
                Collections.shuffle(currentPartition);
                treeBlocks.add(new ArrayList<>(currentPartition));
                currentPartition.clear();
                currentY = treeBlock.getLocation().getBlockY();
            }
            currentPartition.add(treeBlock);
        }

        Collections.shuffle(currentPartition);
        treeBlocks.add(new ArrayList<>(currentPartition));

        TreeDefinition td = this.detectedTree.getTreeDefinition();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!treeBlocks.isEmpty()) {
                    List<ITreeBlock<Block>> partition = treeBlocks.get(0);
                    for (int i = 0; i < 3 && !partition.isEmpty(); i++) {
                        ITreeBlock<Block> treeBlock = partition.remove(0);
                        if (treeBlock.getTreeBlockType().equals(TreeBlockType.LOG)) {
                            if (td.getLogMaterial().stream().noneMatch(x -> x.equals(XMaterial.matchXMaterial(treeBlock.getBlock().getType()))))
                                continue;
                        } else if (treeBlock.getTreeBlockType().equals(TreeBlockType.LEAF)) {
                            if (td.getLeafMaterial().stream().noneMatch(x -> x.equals(XMaterial.matchXMaterial(treeBlock.getBlock().getType()))))
                                continue;
                        }

                        FallingTreeBlock fallingTreeBlock = TreeAnimationCrumble.this.convertToFallingBlock((TreeBlock)treeBlock);
                        if (fallingTreeBlock == null)
                            continue;

                        BlockUtils.toggleGravityFallingBlock(fallingTreeBlock.getBlock(), true);
                        fallingTreeBlock.getBlock().setVelocity(Vector.getRandom().setY(0).subtract(new Vector(0.5, 0, 0.5)).multiply(0.15));
                        TreeAnimationCrumble.this.fallingTreeBlocks.add(fallingTreeBlock);

                        if (TreeAnimationCrumble.this.fallingTreeBlocks == null)
                            TreeAnimationCrumble.this.fallingTreeBlocks = new TreeBlockSet<>(fallingTreeBlock);

                        if (useCustomSound)
                            SoundUtils.playLandingSound(treeBlock);
                        if (useCustomParticles)
                            ParticleUtils.playFallingParticles(treeBlock);
                    }

                    if (partition.isEmpty()) {
                        treeBlocks.remove(0);
                    }
                }

                if (treeBlocks.isEmpty() && TreeAnimationCrumble.this.fallingTreeBlocks.getAllTreeBlocks().isEmpty()) {
                    whenFinished.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
