package com.songoda.ultimatetimber.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.songoda.ultimatetimber.treefall.TreeChecker;

/**
 * Called when a tree will fall
 */
public class TreeFallEvent extends TreeEvent {

    public TreeFallEvent(Player player, TreeChecker treeChecker, Block broke) {
        super(player, treeChecker, broke);
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
