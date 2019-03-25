package com.songoda.ultimatetimber.adapter;

import jdk.nashorn.internal.ir.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface VersionAdapter {

    Set<ItemStack> getBlockDrops(Block block);

    void applyToolDurability(Set<Block> blocks, ItemStack tool);

}
