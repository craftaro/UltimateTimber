package com.songoda.ultimatetimber.adapter.current;

import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import com.songoda.ultimatetimber.tree.FallingTreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

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
    public Collection<ItemStack> getBlockDrops(TreeBlock treeBlock) {
        return null;
    }

    @Override
    public boolean areBlockStatesSimilar(BlockState blockState1, BlockState blockState2) {
        return false;
    }

    @Override
    public void applyToolDurability(TreeBlockSet<Block> treeBlocks, ItemStack tool) {

    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return null;
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
