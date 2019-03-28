package com.songoda.ultimatetimber.adapter;

import com.songoda.ultimatetimber.tree.FallingTreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface VersionAdapter {

    VersionAdapterType getVersionAdapterType();

    /**
     * Loads a Set of TreeDefinitions from the config
     *
     * @return A Set of loaded TreeDefinitions
     */
    Set<TreeDefinition> loadTreeDefinitions();

    /**
     * Get the items that a tree block should drop when it breaks
     *
     * @param treeBlock The tree block broken
     * @param treeDefinition The tree definition to get the drops for
     * @return A Set of ItemStacks that should be dropped
     */
    Set<ItemStack> getTreeBlockDrops(TreeBlock treeBlock, TreeDefinition treeDefinition);

    /**
     * Applies damage to a tool
     *
     * @param treeBlocks The Set of tree blocks that are being broken
     * @param tool The tool to apply damage to
     */
    void applyToolDurability(TreeBlockSet<Block> treeBlocks, ItemStack tool);

    /**
     * Plays particles to indicate a tree has started falling
     */
    void playFallingParticles(TreeBlockSet<Block> treeBlocks);

    /**
     * Plays particles to indicate a tree block has hit the ground
     */
    void playLandingParticles(FallingTreeBlock treeBlock);

    /**
     * Plays a sound to indicate a tree block has started falling
     *
     * @param treeBlocks The TreeBlocks to play the sound for
     */
    void playFallingSound(TreeBlockSet<Block> treeBlocks);

    /**
     * Plays a sound to indicate a tree block has hit the ground
     *
     * @param treeBlock The FallingTreeBlock to play the sound for
     */
    void playLandingSound(FallingTreeBlock treeBlock);

}
