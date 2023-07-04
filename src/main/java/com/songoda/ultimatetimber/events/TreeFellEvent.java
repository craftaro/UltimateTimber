package com.songoda.ultimatetimber.events;

import com.songoda.ultimatetimber.tree.DetectedTree;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a tree fell
 */
public class TreeFellEvent extends TreeEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public TreeFellEvent(Player who, DetectedTree detectedTree) {
        super(who, detectedTree);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
