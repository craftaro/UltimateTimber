package com.songoda.ultimatetimber.adapter;

import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import jdk.nashorn.internal.ir.Block;
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
    void applyToolDurability(Set<TreeBlock> treeBlocks, ItemStack tool);

}
