package com.songoda.ultimatetimber.events;

import com.songoda.ultimatetimber.tree.DetectedTree;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a tree will fall
 */
public class TreeFallEvent extends TreeEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;

    public TreeFallEvent(Player who, DetectedTree detectedTree) {
        super(who, detectedTree);
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
