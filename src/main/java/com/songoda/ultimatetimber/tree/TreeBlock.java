package com.songoda.ultimatetimber.tree;

import com.songoda.ultimatetimber.UltimateTimber;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

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
    public Collection<ItemStack> getDrops() {
        return UltimateTimber.getInstance().getVersionAdapter().getBlockDrops(this);
    }

    @Override
    public TreeBlockType getTreeBlockType() {
        return this.treeBlockType;
    }

}
