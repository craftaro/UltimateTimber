package com.songoda.ultimatetimber.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import com.songoda.ultimatetimber.old_code.TreeChecker;

/**
 * Abstract tree event containing tree's blocks and broke block
 */
public abstract class TreeEvent extends PlayerEvent {
	
	protected final TreeChecker treeChecker;
	protected final Block broke;
	
    public TreeEvent(Player who, TreeChecker treeChecker, Block broke) {
        super(who);
        this.treeChecker = treeChecker;
        this.broke = broke;
    }

    /**
     * Get the tree checker
     * 
     * @return tree checker for the tree
     */
    public TreeChecker getTreeChecker() {
        return treeChecker;
    }

    /**
     * Get the initial block broke by player
     * 
     * @return block broke by player
     */
    public Block getBroke() {
        return broke;
    }
    
}
