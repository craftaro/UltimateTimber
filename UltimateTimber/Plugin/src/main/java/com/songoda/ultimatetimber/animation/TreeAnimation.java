package com.songoda.ultimatetimber.animation;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.tree.DetectedTree;
import com.songoda.ultimatetimber.tree.FallingTreeBlock;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TreeAnimation {

    protected final TreeAnimationType treeAnimationType;
    protected final DetectedTree detectedTree;
    protected final Player player;
    protected final boolean hasSilkTouch;
    protected TreeBlockSet<FallingBlock> fallingTreeBlocks;

    TreeAnimation(TreeAnimationType treeAnimationType, DetectedTree detectedTree, Player player) {
        this.treeAnimationType = treeAnimationType;
        this.detectedTree = detectedTree;
        this.player = player;

        ItemStack itemInHand = UltimateTimber.getInstance().getVersionAdapter().getItemInHand(player);
        this.hasSilkTouch = itemInHand != null && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);

        this.fallingTreeBlocks = new TreeBlockSet<>(); // Should be overridden in any subclasses that need to use it
    }

    /**
     * Plays this tree topple animation
     *
     * @param whenFinished The runnable to run when the animation is done
     */
    public abstract void playAnimation(Runnable whenFinished);

    /**
     * Gets the type of tree animation that this is
     *
     * @return The TreeAnimationType
     */
    public TreeAnimationType getTreeAnimationType() {
        return this.treeAnimationType;
    }

    /**
     * Gets the detected tree
     *
     * @return The detected tree
     */
    public DetectedTree getDetectedTree() {
        return this.detectedTree;
    }

    /**
     * Gets the player who started this tree animation
     *
     * @return The player who started this tree animation
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Checks if this tree animation has silk touch
     *
     * @return True if this animation has silk touch, otherwise false
     */
    public boolean hasSilkTouch() {
        return this.hasSilkTouch;
    }

    /**
     * Gets a TreeBlockSet of the active falling tree blocks
     * May return null if the animation type does not use falling blocks
     *
     * @return A tree block set
     */
    public TreeBlockSet<FallingBlock> getFallingTreeBlocks() {
        return this.fallingTreeBlocks;
    }

    /**
     * Converts a TreeBlock into a FallingTreeBlock
     *
     * @param treeBlock The TreeBlock to convert
     * @return A FallingTreeBlock that has been converted from a TreeBlock
     */
    protected FallingTreeBlock convertToFallingBlock(TreeBlock treeBlock) {
        VersionAdapter versionAdapter = UltimateTimber.getInstance().getVersionAdapter();
        Location location = treeBlock.getLocation().clone().add(0.5, 0, 0.5);
        Block block = treeBlock.getBlock();

        if (block.getType().equals(Material.AIR)) {
            this.replaceBlock(treeBlock);
            return null;
        }

        FallingBlock fallingBlock = versionAdapter.spawnFallingBlock(location, block);
        UltimateTimber.getInstance().getVersionAdapter().configureFallingBlock(fallingBlock);

        FallingTreeBlock fallingTreeBlock = new FallingTreeBlock(fallingBlock, treeBlock.getTreeBlockType());
        this.replaceBlock(treeBlock);
        return fallingTreeBlock;
    }

    /**
     * Replaces a given block with a new one
     *
     * @param treeBlock The tree block to replace
     */
    public void replaceBlock(TreeBlock treeBlock) {
        treeBlock.getBlock().setType(Material.AIR);
        UltimateTimber.getInstance().getSaplingManager().replantSapling(this.detectedTree.getTreeDefinition(), treeBlock);
    }

    /**
     * Removes a falling block from the animation
     *
     * @param fallingBlock The FallingBlock to remove
     */
    public void removeFallingBlock(FallingBlock fallingBlock) {
        for (ITreeBlock<FallingBlock> fallingTreeBlock : this.fallingTreeBlocks.getAllTreeBlocks()) {
            if (fallingTreeBlock.getBlock().equals(fallingBlock)) {
                this.fallingTreeBlocks.remove(fallingTreeBlock);
                return;
            }
        }
    }

}
