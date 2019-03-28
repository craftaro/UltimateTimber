package com.songoda.ultimatetimber.events;

import com.songoda.ultimatetimber.tree.TreeBlockSet;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

/**
 * Abstract tree event containing tree's blocks and broke block
 */
public abstract class TreeEvent extends PlayerEvent {
	
	protected final TreeBlockSet<Block> treeBlocks;

    public TreeEvent(Player player, TreeBlockSet<Block> treeBlocks) {
        super(player);
        this.treeBlocks = treeBlocks;
    }

    /**
     * Get the tree blocks
     * 
     * @return tree checker for the tree
     */
    public TreeBlockSet<Block> getTreeBlocks() {
        return this.treeBlocks;
    }
    
}
