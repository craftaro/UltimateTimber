package com.craftaro.ultimatetimber.tree;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Objects;

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
    public Location getLocation() {
        return this.block.getLocation();
    }

    @Override
    public TreeBlockType getTreeBlockType() {
        return this.treeBlockType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.block, this.treeBlockType);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TreeBlock)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        TreeBlock oTreeBlock = (TreeBlock) obj;
        return oTreeBlock.block.equals(this.block) && oTreeBlock.treeBlockType == this.treeBlockType;
    }
}
