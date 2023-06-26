package com.craftaro.ultimatetimber.listeners;

import com.craftaro.ultimatetimber.UltimateTimber;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListeners implements Listener {

    private final UltimateTimber plugin;
    public ConnectionListeners(UltimateTimber plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getPlayerManager().loadPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerManager().savePlayer(event.getPlayer());
    }
}
