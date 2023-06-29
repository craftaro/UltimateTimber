package com.songoda.ultimatetimber.animation;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.manager.ConfigurationManager;
import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.FallingTreeBlock;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeBlockType;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import com.songoda.ultimatetimber.utils.BlockUtils;
import com.songoda.ultimatetimber.utils.ParticleUtils;
import com.songoda.ultimatetimber.utils.SoundUtils;
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
        UltimateTimber ultimateTimber = UltimateTimber.getInstance();

        boolean useCustomSound = ConfigurationManager.Setting.USE_CUSTOM_SOUNDS.getBoolean();
        boolean useCustomParticles = ConfigurationManager.Setting.USE_CUSTOM_PARTICLES.getBoolean();

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
                            if (td.getLogMaterial().stream().noneMatch(x -> x.equals(CompatibleMaterial.getMaterial(treeBlock.getBlock()))))
                                continue;
                        } else if (treeBlock.getTreeBlockType().equals(TreeBlockType.LEAF)) {
                            if (td.getLeafMaterial().stream().noneMatch(x -> x.equals(CompatibleMaterial.getMaterial(treeBlock.getBlock()))))
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
        }.runTaskTimer(ultimateTimber, 0, 1);
    }
}

