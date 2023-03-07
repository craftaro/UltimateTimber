package com.songoda.ultimatetimber.events;

import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class TreeDamageEvent extends PlayerEvent implements Cancellable {

    FallingBlock attacker;
    public TreeDamageEvent(FallingBlock attacker, Player victim) {
        super(victim);
        this.attacker = attacker;
    }

    public FallingBlock getAttacker() {return this.attacker;}

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
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }
}
