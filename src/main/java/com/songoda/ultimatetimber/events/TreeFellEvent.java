package com.songoda.ultimatetimber.events;

import com.songoda.ultimatetimber.tree.TreeBlockSet;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a tree fell
 */
public class TreeFellEvent extends TreeEvent {

    public TreeFellEvent(Player player, TreeBlockSet<Block> treeBlocks) {
        super(player, treeBlocks);
    }

    private static final HandlerList handlers = new HandlerList();
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
	
}
