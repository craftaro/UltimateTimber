package com.songoda.ultimatetimber.adapter.current;

import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import com.songoda.ultimatetimber.tree.FallingTreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class CurrentAdapter implements VersionAdapter {

    @Override
    public VersionAdapterType getVersionAdapterType() {
        return VersionAdapterType.CURRENT;
    }

    @Override
    public BlockState parseBlockStateFromString(String blockStateString) {
        return null;
    }

    @Override
    public ItemStack parseItemStackFromString(String itemStackString) {
        return null;
    }

    @Override
    public Set<ItemStack> getTreeBlockDrops(TreeBlock treeBlock, TreeDefinition treeDefinition) {
        return null;
    }

    @Override
    public void applyToolDurability(TreeBlockSet<Block> treeBlocks, ItemStack tool) {

    }

    @Override
    public void playFallingParticles(TreeBlockSet<Block> treeBlocks) {

    }

    @Override
    public void playLandingParticles(FallingTreeBlock treeBlock) {

    }

    @Override
    public void playFallingSound(TreeBlockSet<Block> treeBlocks) {

    }

    @Override
    public void playLandingSound(FallingTreeBlock treeBlock) {

    }

}
