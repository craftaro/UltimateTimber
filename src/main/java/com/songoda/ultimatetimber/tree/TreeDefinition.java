package com.songoda.ultimatetimber.tree;

import com.songoda.core.compatibility.CompatibleMaterial;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

public class TreeDefinition {

    private final String key;
    private final Set<CompatibleMaterial> logMaterial, leafMaterial, plantableSoilMaterial;
    private final CompatibleMaterial saplingMaterial;
    private final double maxLogDistanceFromTrunk;
    private final int maxLeafDistanceFromLog;
    private final boolean detectLeavesDiagonally;
    private final boolean dropOriginalLog, dropOriginalLeaf;
    private final Set<TreeLoot> logLoot, leafLoot, entireTreeLoot;
    private final Set<ItemStack> requiredTools;
    private final boolean requiredAxe;

    public TreeDefinition(String key, Set<CompatibleMaterial> logMaterial, Set<CompatibleMaterial> leafMaterial, CompatibleMaterial saplingMaterial,
                          Set<CompatibleMaterial> plantableSoilMaterial, double maxLogDistanceFromTrunk, int maxLeafDistanceFromLog,
                          boolean detectLeavesDiagonally, boolean dropOriginalLog, boolean dropOriginalLeaf, Set<TreeLoot> logLoot,
                          Set<TreeLoot> leafLoot, Set<TreeLoot> entireTreeLoot, Set<ItemStack> requiredTools, boolean requiredAxe) {
        this.key = key;
        this.logMaterial = logMaterial;
        this.leafMaterial = leafMaterial;
        this.saplingMaterial = saplingMaterial;
        this.plantableSoilMaterial = plantableSoilMaterial;
        this.maxLogDistanceFromTrunk = maxLogDistanceFromTrunk;
        this.maxLeafDistanceFromLog = maxLeafDistanceFromLog;
        this.detectLeavesDiagonally = detectLeavesDiagonally;
        this.dropOriginalLog = dropOriginalLog;
        this.dropOriginalLeaf = dropOriginalLeaf;
        this.logLoot = logLoot;
        this.leafLoot = leafLoot;
        this.entireTreeLoot = entireTreeLoot;
        this.requiredTools = requiredTools;
        this.requiredAxe = requiredAxe;
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
     * Gets a set of valid log block data for this TreeDefinition
     *
     * @return A Set of CompatibleMaterial
     */
    public Set<CompatibleMaterial> getLogMaterial() {
        return Collections.unmodifiableSet(this.logMaterial);
    }

    /**
     * Gets a set of valid leaf block data for this TreeDefinition
     *
     * @return A Set of CompatibleMaterial
     */
    public Set<CompatibleMaterial> getLeafMaterial() {
        return Collections.unmodifiableSet(this.leafMaterial);
    }

    /**
     * Gets the sapling block data of this TreeDefinition
     *
     * @return An CompatibleMaterial instance for the sapling
     */
    public CompatibleMaterial getSaplingMaterial() {
        return this.saplingMaterial;
    }

    /**
     * Gets a set of plantable soil block data for this TreeDefinition
     *
     * @return A Set of CompatibleMaterial
     */
    public Set<CompatibleMaterial> getPlantableSoilMaterial() {
        return Collections.unmodifiableSet(this.plantableSoilMaterial);
    }

    /**
     * Gets the max distance away a log can be from the tree trunk in order to be part of the tree
     *
     * @return The max distance a log can be from the tree trunk
     */
    public double getMaxLogDistanceFromTrunk() {
        return this.maxLogDistanceFromTrunk;
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
     * Gets if tree detection should check for leaves diagonally
     *
     * @return True if leaves should be searched for diagonally, otherwise false
     */
    public boolean shouldDetectLeavesDiagonally() {
        return this.detectLeavesDiagonally;
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
     * Gets the loot for this TreeDefinition
     *
     * @return A Set of TreeLoot
     */
    public Set<TreeLoot> getEntireTreeLoot() {
        return Collections.unmodifiableSet(this.entireTreeLoot);
    }

    /**
     * Gets the tools that can be used to activate this tree topple
     *
     * @return A Set of ItemStacks
     */
    public Set<ItemStack> getRequiredTools() {
        return Collections.unmodifiableSet(this.requiredTools);
    }

    /**
     * Returns whether or not does this TreeDefinition require a custom axe.
     *
     * @return True if the TreeDefinition requires a custom axe
     */
    public boolean isRequiredAxe() {
        return requiredAxe;
    }
}
