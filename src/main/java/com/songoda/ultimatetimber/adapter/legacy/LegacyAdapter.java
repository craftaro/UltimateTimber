package com.songoda.ultimatetimber.adapter.legacy;

import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeDefinition;
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
    public void applyToolDurability(Set<TreeBlock> blocks, ItemStack tool) {

    }

}
