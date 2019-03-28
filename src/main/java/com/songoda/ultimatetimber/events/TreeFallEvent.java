package com.songoda.ultimatetimber.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.songoda.ultimatetimber.old_code.TreeChecker;

/**
 * Called when a tree will fall
 */
public class TreeFallEvent extends TreeEvent implements Cancellable {
	
	private boolean cancelled = false;
	
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

    @Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}
