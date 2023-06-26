package com.craftaro.ultimatetimber.events;

import com.craftaro.ultimatetimber.tree.DetectedTree;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a tree will fall
 */
public class TreePreFallEvent extends TreeEvent implements Cancellable {

    private boolean cancelled = false;

    public TreePreFallEvent(Player player, DetectedTree detectedTree) {
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

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}