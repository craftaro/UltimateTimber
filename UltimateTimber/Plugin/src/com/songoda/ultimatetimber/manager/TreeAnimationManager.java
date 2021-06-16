package com.songoda.ultimatetimber.manager;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.animation.TreeAnimation;
import com.songoda.ultimatetimber.animation.TreeAnimationCrumble;
import com.songoda.ultimatetimber.animation.TreeAnimationDisintegrate;
import com.songoda.ultimatetimber.animation.TreeAnimationFancy;
import com.songoda.ultimatetimber.animation.TreeAnimationNone;
import com.songoda.ultimatetimber.animation.TreeAnimationType;
import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import com.songoda.ultimatetimber.utils.ParticleUtils;
import com.songoda.ultimatetimber.utils.SoundUtils;
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

public class TreeAnimationManager extends Manager implements Listener, Runnable {

    private final Set<TreeAnimation> activeAnimations;
    private final int taskId;

    public TreeAnimationManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        this.activeAnimations = new HashSet<>();
        this.taskId = -1;
        Bukkit.getPluginManager().registerEvents(this, ultimateTimber);
        Bukkit.getScheduler().runTaskTimer(this.plugin, this, 0, 1L);
    }

    @Override
    public void reload() {
        this.activeAnimations.clear();
    }

    @Override
    public void disable() {
        this.activeAnimations.clear();
        Bukkit.getScheduler().cancelTask(this.taskId);
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
        switch (TreeAnimationType.fromString(ConfigurationManager.Setting.TREE_ANIMATION_TYPE.getString())) {
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
        boolean useCustomSound = ConfigurationManager.Setting.USE_CUSTOM_SOUNDS.getBoolean();
        boolean useCustomParticles = ConfigurationManager.Setting.USE_CUSTOM_PARTICLES.getBoolean();
        TreeDefinition treeDefinition = treeAnimation.getDetectedTree().getTreeDefinition();

        if (useCustomParticles)
            ParticleUtils.playLandingParticles(treeBlock);
        if (useCustomSound)
            SoundUtils.playLandingSound(treeBlock);

        Block block = treeBlock.getLocation().subtract(0, 1, 0).getBlock();
        if (ConfigurationManager.Setting.FRAGILE_BLOCKS.getStringList().contains(block.getType().toString())) {
            block.getWorld().dropItemNaturally(block.getLocation(), CompatibleMaterial.getMaterial(block).getItem());
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

        if (ConfigurationManager.Setting.FALLING_BLOCKS_DEAL_DAMAGE.getBoolean()) {
            int damage = ConfigurationManager.Setting.FALLING_BLOCK_DAMAGE.getInt();
            for (Entity entity : fallingBlock.getNearbyEntities(0.5, 0.5, 0.5)) {
                if (!(entity instanceof LivingEntity)) continue;
                ((LivingEntity) entity).damage(damage, fallingBlock);
            }
        }

        if (ConfigurationManager.Setting.SCATTER_TREE_BLOCKS_ON_GROUND.getBoolean()) {
            TreeAnimation treeAnimation = this.getAnimationForBlock(fallingBlock);
            if (treeAnimation != null) {
                treeAnimation.removeFallingBlock(fallingBlock);
                return;
            }
        }

        event.setCancelled(true);
    }
}
