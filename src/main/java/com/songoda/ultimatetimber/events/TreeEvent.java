package com.songoda.ultimatetimber.events;

import com.songoda.ultimatetimber.tree.DetectedTree;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

/**
 * Abstract tree event containing tree's blocks and broke block
 */
public abstract class TreeEvent extends PlayerEvent {

	protected final DetectedTree detectedTree;

    public TreeEvent(Player player, DetectedTree detectedTree) {
        super(player);
        this.detectedTree = detectedTree;
    }

    /**
     * Get the tree blocks
     * 
     * @return The blocks that are part of the tree
     */
    public DetectedTree getDetectedTree() {
        return this.detectedTree;
    }
    
}
