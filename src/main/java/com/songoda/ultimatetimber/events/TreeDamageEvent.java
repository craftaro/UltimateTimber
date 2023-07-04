package com.songoda.ultimatetimber.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class TreeDamageEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled = false;
    private FallingBlock blockAttacker = null;
    private Player playerAttacker = null;
    private static final HandlerList handlers = new HandlerList();

    /**
     * Represents a TreeDamage event.
     */
    public TreeDamageEvent(FallingBlock attacker, Player victim) {
        super(victim);
        this.blockAttacker = attacker;
    }

    // Hoping this one is used whenever possible
    /**
     * Represents a TreeDamage event.
     */
    public TreeDamageEvent(Player attacker, Player victim) {
        super(victim);
        this.playerAttacker = attacker;
    }

    /**
     * @return the attacker as either FallingBlock or Player
     */
    public Entity getAttacker() {
        if (this.blockAttacker != null)
            return this.blockAttacker;
        if (this.playerAttacker != null)
            return this.playerAttacker;
        return null;
    }

    /**
     * Get Player damaged by this event. This method is only here for clarification
     */
    public Player getVictim() {
        return this.getPlayer();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelState) {this.cancelled = cancelState;}
}
