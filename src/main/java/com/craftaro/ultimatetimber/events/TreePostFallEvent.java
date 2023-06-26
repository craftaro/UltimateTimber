package com.craftaro.ultimatetimber.events;

import com.craftaro.ultimatetimber.tree.DetectedTree;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a tree fell
 */
public class TreePostFallEvent extends TreeEvent {

    public TreePostFallEvent(Player player, DetectedTree detectedTree) {
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