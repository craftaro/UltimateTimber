package com.songoda.ultimatetimber.adapter.legacy;

import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import com.songoda.ultimatetimber.tree.FallingTreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class LegacyAdapter implements VersionAdapter {

    @Override
    public VersionAdapterType getVersionAdapterType() {
        return VersionAdapterType.LEGACY;
    }

    @Override
    public Set<TreeDefinition> loadTreeDefinitions() {
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
