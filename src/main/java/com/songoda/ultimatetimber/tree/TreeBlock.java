package com.songoda.ultimatetimber.tree;

import org.bukkit.block.Block;

public class TreeBlock implements ITreeBlock<Block> {

    private final Block block;
    private final TreeBlockType treeBlockType;

    public TreeBlock(Block block, TreeBlockType treeBlockType) {
        this.block = block;
        this.treeBlockType = treeBlockType;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    @Override
    public TreeBlockType getTreeBlockType() {
        return this.treeBlockType;
    }

}
