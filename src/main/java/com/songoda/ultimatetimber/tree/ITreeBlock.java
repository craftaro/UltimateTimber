package com.songoda.ultimatetimber.tree;

public interface ITreeBlock<BlockType> {

    /**
     * Gets the block this TreeBlock represents
     *
     * @return The Block for this TreeBlock
     */
    BlockType getBlock();

    /**
     * Gets what type of TreeBlock this is
     *
     * @return The TreeBlockType
     */
    TreeBlockType getTreeBlockType();

}
