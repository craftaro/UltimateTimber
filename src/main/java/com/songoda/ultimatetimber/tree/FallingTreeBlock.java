package com.songoda.ultimatetimber.tree;

import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class FallingTreeBlock implements ITreeBlock<FallingBlock> {

    private final FallingBlock fallingBlock;
    private final TreeBlockType treeBlockType;
    private final Collection<ItemStack> drops;

    public FallingTreeBlock(TreeBlock originalTreeBlock, FallingBlock fallingBlock, TreeBlockType treeBlockType) {
        this.fallingBlock = fallingBlock;
        this.treeBlockType = treeBlockType;
        this.drops = originalTreeBlock.getDrops();
    }

    @Override
    public FallingBlock getBlock() {
        return this.fallingBlock;
    }

    @Override
    public Location getLocation() {
        return this.fallingBlock.getLocation();
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return this.drops;
    }

    @Override
    public TreeBlockType getTreeBlockType() {
        return this.treeBlockType;
    }

}
