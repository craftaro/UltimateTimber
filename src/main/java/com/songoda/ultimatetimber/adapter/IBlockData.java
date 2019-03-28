package com.songoda.ultimatetimber.adapter;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface IBlockData {

    /**
     * Gets the Material of the BlockData
     *
     * @return The Material
     */
    Material getMaterial();

    /**
     * Gets the data of the BlockData
     *
     * @return The data
     */
    byte getData();

    /**
     * Compares this IBlockData with another one to see if they are similar
     *
     * @return True if they are similar, otherwise false
     */
    boolean isSimilar(IBlockData otherBlockData);

    /**
     * Compares this IBlockData with a block to see if they are similar
     *
     * @return True if they are similar, otherwise false
     */
    boolean isSimilar(Block block);

}
