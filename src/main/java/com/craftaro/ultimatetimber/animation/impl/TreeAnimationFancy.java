package com.craftaro.ultimatetimber.animation.impl;

import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.ultimatetimber.animation.TreeAnimation;
import com.craftaro.ultimatetimber.managers.TreeAnimationManager;
import com.craftaro.ultimatetimber.tree.*;
import com.craftaro.ultimatetimber.utils.BlockUtils;
import com.craftaro.ultimatetimber.utils.ParticleUtils;
import com.craftaro.ultimatetimber.utils.SoundUtils;
import com.craftaro.ultimatetimber.animation.TreeAnimationType;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TreeAnimationFancy extends TreeAnimation {

    public TreeAnimationFancy(DetectedTree detectedTree, Player player) {
        super(TreeAnimationType.FANCY, detectedTree, player);
    }

    @Override
    public void playAnimation(Runnable whenFinished) {
        UltimateTimber plugin = UltimateTimber.getInstance();

        boolean useCustomSound = plugin.getMainConfig().getBoolean("Settings.Use Custom Sounds");
        boolean useCustomParticles = plugin.getMainConfig().getBoolean("Settings.Use Custom Particles");

        ITreeBlock<Block> initialTreeBlock = this.detectedTree.getDetectedTreeBlocks().getInitialLogBlock();
        FallingTreeBlock initialFallingBlock = this.convertToFallingBlock((TreeBlock)this.detectedTree.getDetectedTreeBlocks().getInitialLogBlock());

        if (useCustomSound)
            SoundUtils.playFallingSound(initialTreeBlock);

        Vector velocityVector = initialTreeBlock.getLocation().clone().subtract(this.player.getLocation().clone()).toVector().normalize().setY(0);

        this.fallingTreeBlocks = new TreeBlockSet<>(initialFallingBlock);
        for (ITreeBlock<Block> treeBlock : this.detectedTree.getDetectedTreeBlocks().getAllTreeBlocks()) {
            FallingTreeBlock fallingTreeBlock = this.convertToFallingBlock((TreeBlock)treeBlock);
            if (fallingTreeBlock == null)
                continue;

            FallingBlock fallingBlock = fallingTreeBlock.getBlock();
            this.fallingTreeBlocks.add(fallingTreeBlock);

            if (useCustomParticles)
                ParticleUtils.playFallingParticles(treeBlock);

            double multiplier = (treeBlock.getLocation().getY() - this.player.getLocation().getY()) * 0.05;
            fallingBlock.setVelocity(velocityVector.clone().multiply(multiplier));
            fallingBlock.setVelocity(fallingBlock.getVelocity().multiply(0.3));
        }

        new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                if (this.timer == 0) {
                    for (ITreeBlock<FallingBlock> fallingTreeBlock : TreeAnimationFancy.this.fallingTreeBlocks.getAllTreeBlocks()) {
                        FallingBlock fallingBlock = fallingTreeBlock.getBlock();
                        BlockUtils.toggleGravityFallingBlock(fallingBlock, true);
                        fallingBlock.setVelocity(fallingBlock.getVelocity().multiply(1.5));
                    }
                }

                if (TreeAnimationFancy.this.fallingTreeBlocks.getAllTreeBlocks().isEmpty()) {
                    whenFinished.run();
                    this.cancel();
                    return;
                }

                for (ITreeBlock<FallingBlock> fallingTreeBlock : TreeAnimationFancy.this.fallingTreeBlocks.getAllTreeBlocks()) {
                    FallingBlock fallingBlock = fallingTreeBlock.getBlock();
                    fallingBlock.setVelocity(fallingBlock.getVelocity().clone().subtract(new Vector(0, 0.05, 0)));
                }

                this.timer++;

                if (this.timer > 4 * 20) {
                    TreeAnimationManager treeAnimationManager = plugin.getTreeAnimationManager();
                    for (ITreeBlock<FallingBlock> fallingTreeBlock : TreeAnimationFancy.this.fallingTreeBlocks.getAllTreeBlocks())
                        treeAnimationManager.runFallingBlockImpact(TreeAnimationFancy.this, fallingTreeBlock);
                    whenFinished.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }

}