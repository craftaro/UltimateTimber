package com.songoda.ultimatetimber.tree;

import org.bukkit.block.Block;

public class TreeBlock {

    private final Block block;
    private final TreeBlockType treeBlockType;

    public TreeBlock(Block block, TreeBlockType treeBlockType) {
        this.block = block;
        this.treeBlockType = treeBlockType;
    }

    public Block getBlock() {
        return this.block;
    }

    public TreeBlockType getTreeBlockType() {
        return this.treeBlockType;
    }

}
