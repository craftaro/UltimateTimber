package com.craftaro.ultimatetimber.managers;

import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.ultimatetimber.tree.DetectedTree;
import com.craftaro.ultimatetimber.tree.ITreeBlock;
import com.craftaro.ultimatetimber.tree.TreeDefinition;
import com.craftaro.ultimatetimber.utils.ParticleUtils;
import com.craftaro.ultimatetimber.utils.SoundUtils;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatetimber.animation.TreeAnimation;
import com.craftaro.ultimatetimber.animation.TreeAnimationType;
import com.craftaro.ultimatetimber.animation.impl.TreeAnimationCrumble;
import com.craftaro.ultimatetimber.animation.impl.TreeAnimationDisintegrate;
import com.craftaro.ultimatetimber.animation.impl.TreeAnimationFancy;
import com.craftaro.ultimatetimber.animation.impl.TreeAnimationNone;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.HashSet;
import java.util.Set;

public class TreeAnimationManager implements Listener, Runnable {

    private final UltimateTimber plugin;
    private final Set<TreeAnimation> activeAnimations;
    private final int taskId;

    public TreeAnimationManager(UltimateTimber plugin) {
        this.plugin = plugin;
        this.activeAnimations = new HashSet<>();
        this.taskId = -1;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, this, 0, 1L);
    }

    @Override
    public void run() {
        for (TreeAnimation treeAnimation : this.activeAnimations) {
            Set<ITreeBlock<FallingBlock>> groundedBlocks = new HashSet<>();
            for (ITreeBlock<FallingBlock> fallingTreeBlock : treeAnimation.getFallingTreeBlocks().getAllTreeBlocks()) {
                FallingBlock fallingBlock = fallingTreeBlock.getBlock();
                if (fallingBlock.isDead() || ServerVersion.isServerVersionAtLeast(ServerVersion.V1_17) && fallingBlock.isOnGround())
                    groundedBlocks.add(fallingTreeBlock);
            }

            for (ITreeBlock<FallingBlock> fallingBlock : groundedBlocks) {
                this.runFallingBlockImpact(treeAnimation, fallingBlock);
                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_17))
                    fallingBlock.getBlock().remove();
                treeAnimation.getFallingTreeBlocks().remove(fallingBlock);
            }
        }
    }

    /**
     * Plays an animation for toppling a tree
     *
     * @param detectedTree The DetectedTree
     * @param player       The Player who toppled the tree
     */
    public void runAnimation(DetectedTree detectedTree, Player player) {
        switch (TreeAnimationType.fromString(plugin.getMainConfig().getString("Settings.Tree Animation Type"))) {
            case FANCY:
                this.registerTreeAnimation(new TreeAnimationFancy(detectedTree, player));
                break;
            case DISINTEGRATE:
                this.registerTreeAnimation(new TreeAnimationDisintegrate(detectedTree, player));
                break;
            case CRUMBLE:
                this.registerTreeAnimation(new TreeAnimationCrumble(detectedTree, player));
                break;
            case NONE:
                this.registerTreeAnimation(new TreeAnimationNone(detectedTree, player));
                break;
        }
    }

    /**
     * Checks if the given block is in an animation
     *
     * @param block The block to check
     */
    public boolean isBlockInAnimation(Block block) {
        for (TreeAnimation treeAnimation : this.activeAnimations)
            for (ITreeBlock<Block> treeBlock : treeAnimation.getDetectedTree().getDetectedTreeBlocks().getAllTreeBlocks())
                if (treeBlock.getBlock().equals(block))
                    return true;
        return false;
    }

    /**
     * Checks if the given falling block is in an animation
     *
     * @param fallingBlock The falling block to check
     */
    public boolean isBlockInAnimation(FallingBlock fallingBlock) {
        for (TreeAnimation treeAnimation : this.activeAnimations)
            for (ITreeBlock<FallingBlock> treeBlock : treeAnimation.getFallingTreeBlocks().getAllTreeBlocks())
                if (treeBlock.getBlock().equals(fallingBlock))
                    return true;
        return false;
    }

    /**
     * Gets a TreeAnimation that a given falling block is in
     *
     * @return A TreeAnimation
     */
    private TreeAnimation getAnimationForBlock(FallingBlock fallingBlock) {
        for (TreeAnimation treeAnimation : this.activeAnimations)
            for (ITreeBlock<FallingBlock> treeBlock : treeAnimation.getFallingTreeBlocks().getAllTreeBlocks())
                if (treeBlock.getBlock().equals(fallingBlock))
                    return treeAnimation;
        return null;
    }

    /**
     * Registers and runs a tree animation
     */
    private void registerTreeAnimation(TreeAnimation treeAnimation) {
        this.activeAnimations.add(treeAnimation);
        treeAnimation.playAnimation(() -> this.activeAnimations.remove(treeAnimation));
    }

    /**
     * Reacts to a falling block hitting the ground
     *
     * @param treeAnimation The tree animation for the falling block
     * @param treeBlock     The tree block to impact
     */
    public void runFallingBlockImpact(TreeAnimation treeAnimation, ITreeBlock<FallingBlock> treeBlock) {
        TreeDefinitionManager treeDefinitionManager = this.plugin.getTreeDefinitionManager();
        boolean useCustomSound = plugin.getMainConfig().getBoolean("Settings.Use Custom Sounds");
        boolean useCustomParticles = plugin.getMainConfig().getBoolean("Settings.Use Custom Particles");
        TreeDefinition treeDefinition = treeAnimation.getDetectedTree().getTreeDefinition();

        if (useCustomParticles)
            ParticleUtils.playLandingParticles(treeBlock);
        if (useCustomSound)
            SoundUtils.playLandingSound(treeBlock);

        Block block = treeBlock.getLocation().subtract(0, 1, 0).getBlock();
        if (plugin.getMainConfig().getStringList("Settings.Fragile Blocks").contains(block.getType().toString())) {
            block.getWorld().dropItemNaturally(block.getLocation(), XMaterial.matchXMaterial(block.getType()).parseItem());
            block.breakNaturally();
        }

        treeDefinitionManager.dropTreeLoot(treeDefinition, treeBlock, treeAnimation.getPlayer(), treeAnimation.hasSilkTouch(), false);
        this.plugin.getSaplingManager().replantSaplingWithChance(treeDefinition, treeBlock);
        treeAnimation.getFallingTreeBlocks().remove(treeBlock);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFallingBlockLand(EntityChangeBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.FALLING_BLOCK))
            return;

        FallingBlock fallingBlock = (FallingBlock) event.getEntity();
        if (!this.isBlockInAnimation(fallingBlock))
            return;

        if (plugin.getMainConfig().getBoolean("Settings.Falling Blocks Deal Damage")) {
            int damage = plugin.getMainConfig().getInt("Settings.Falling Block Damage");
            for (Entity entity : fallingBlock.getNearbyEntities(0.5, 0.5, 0.5)) {
                if (!(entity instanceof LivingEntity)) continue;
                ((LivingEntity) entity).damage(damage, fallingBlock);
            }
        }

        if (plugin.getMainConfig().getBoolean("Settings.Scatter Tree Blocks On Ground")) {
            TreeAnimation treeAnimation = this.getAnimationForBlock(fallingBlock);
            if (treeAnimation != null) {
                treeAnimation.removeFallingBlock(fallingBlock);
                return;
            }
        }

        event.setCancelled(true);
    }
}