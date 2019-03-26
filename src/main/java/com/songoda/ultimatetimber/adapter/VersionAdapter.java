package com.songoda.ultimatetimber.adapter;

import jdk.nashorn.internal.ir.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface VersionAdapter {

    /**
     * Get the items that a block should drop when it breaks
     *
     * @param block The target block
     * @return A Set of ItemStacks that should be dropped
     */
    Set<ItemStack> getBlockDrops(Block block);

    /**
     * Applies damage to a tool
     *
     * @param blocks The Set of blocks that are being broken
     * @param tool The tool to apply damage to
     */
    void applyToolDurability(Set<Block> blocks, ItemStack tool);



}
