package com.songoda.ultimatetimber.tree;

import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

public class TreeDefinition {

    private final String key;
    private final Set<BlockState> logBlockStates, leafBlockStates;
    private final BlockState saplingBlockState;
    private final int maxLeafDistanceFromLog;
    private final boolean dropOriginalLog, dropOriginalLeaf;
    private final Set<TreeLoot> logLoot, leafLoot;
    private final Set<ItemStack> requiredTools;

    public TreeDefinition(String key, Set<BlockState> logBlocks, Set<BlockState> leafBlocks, BlockState saplingBlockState,
                          int maxLeafDistanceFromLog, boolean dropOriginalLog, boolean dropOriginalLeaf,
                          Set<TreeLoot> logLoot, Set<TreeLoot> leafLoot, Set<ItemStack> requiredTools) {
        this.key = key;
        this.logBlockStates = logBlocks;
        this.leafBlockStates = leafBlocks;
        this.saplingBlockState = saplingBlockState;
        this.maxLeafDistanceFromLog = maxLeafDistanceFromLog;
        this.dropOriginalLog = dropOriginalLog;
        this.dropOriginalLeaf = dropOriginalLeaf;
        this.logLoot = logLoot;
        this.leafLoot = leafLoot;
        this.requiredTools = requiredTools;
    }

    /**
     * Gets the key of this TreeDefinition in the config
     *
     * @return The key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Gets a set of valid log block states for this TreeDefinition
     *
     * @return A Set of BlockStates
     */
    public Set<BlockState> getLogBlockStates() {
        return Collections.unmodifiableSet(this.logBlockStates);
    }

    /**
     * Gets a set of valid leaf block states for this TreeDefinition
     *
     * @return A Set of BlockStates
     */
    public Set<BlockState> getLeafBlockStates() {
        return Collections.unmodifiableSet(this.leafBlockStates);
    }

    /**
     * Gets the sapling block state of this TreeDefinition
     *
     * @return A BlockState for the sapling
     */
    public BlockState getSaplingBlockState() {
        return this.saplingBlockState;
    }

    /**
     * Gets the max distance away a leaf can be from a log in order to be part of the tree
     *
     * @return The max distance a leaf can be from a log
     */
    public int getMaxLeafDistanceFromLog() {
        return this.maxLeafDistanceFromLog;
    }

    /**
     * Gets if the logs of this tree should drop their original block
     *
     * @return True if the original log block should be dropped, otherwise false
     */
    public boolean shouldDropOriginalLog() {
        return this.dropOriginalLog;
    }

    /**
     * Gets if the leaves of this tree should drop their original block
     *
     * @return True if the original leaf block should be dropped, otherwise false
     */
    public boolean shouldDropOriginalLeaf() {
        return this.dropOriginalLeaf;
    }

    /**
     * Gets the log loot for this TreeDefinition
     *
     * @return A Set of TreeLoot
     */
    public Set<TreeLoot> getLogLoot() {
        return Collections.unmodifiableSet(this.logLoot);
    }

    /**
     * Gets the leaf loot for this TreeDefinition
     *
     * @return A Set of TreeLoot
     */
    public Set<TreeLoot> getLeafLoot() {
        return Collections.unmodifiableSet(this.leafLoot);
    }

    /**
     * Gets the tools that can be used to activate this tree topple
     *
     * @return A Set of ItemStacks
     */
    public Set<ItemStack> getRequiredTools() {
        return Collections.unmodifiableSet(this.requiredTools);
    }

}
