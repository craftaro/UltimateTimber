package com.songoda.ultimatetimber.tree;

import org.bukkit.block.Block;

public class DetectedTree {

    private TreeDefinition treeDefinition;
    private TreeBlockSet<Block> detectedTreeBlocks;

    public DetectedTree(TreeDefinition treeDefinition, TreeBlockSet<Block> detectedTreeBlocks) {
        this.treeDefinition = treeDefinition;
        this.detectedTreeBlocks = detectedTreeBlocks;
    }

    /**
     * Gets the TreeDefinition of this detected tree
     *
     * @return The TreeDefinition of this detected tree
     */
    public TreeDefinition getTreeDefinition() {
        return this.treeDefinition;
    }

    /**
     * Gets the blocks that were detected as part of this tree
     *
     * @return A TreeBlockSet of detected Blocks
     */
    public TreeBlockSet<Block> getDetectedTreeBlocks() {
        return this.detectedTreeBlocks;
    }

}
