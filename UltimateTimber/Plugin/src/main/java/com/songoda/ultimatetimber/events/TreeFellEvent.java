package com.songoda.ultimatetimber.events;

import com.songoda.ultimatetimber.tree.DetectedTree;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a tree fell
 */
public class TreeFellEvent extends TreeEvent {

    public TreeFellEvent(Player player, DetectedTree detectedTree) {
        super(player, detectedTree);
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
