package com.songoda.ultimatetimber.tree;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface ITreeBlock<BlockType> {

    /**
     * Gets the block this TreeBlock represents
     *
     * @return The Block for this TreeBlock
     */
    BlockType getBlock();

    /**
     * Gets the location of this TreeBlock
     *
     * @return The Location of this TreeBlock
     */
    Location getLocation();

    /**
     * Gets the items this TreeBlock naturally drops
     *
     * @return The ItemStack this TreeBlock naturally drops
     */
    Collection<ItemStack> getDrops();

    /**
     * Gets what type of TreeBlock this is
     *
     * @return The TreeBlockType
     */
    TreeBlockType getTreeBlockType();

}
