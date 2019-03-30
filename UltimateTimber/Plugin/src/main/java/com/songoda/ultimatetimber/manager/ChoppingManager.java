package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChoppingManager extends Manager {

    private Set<UUID> disabledPlayers;

    public ChoppingManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        this.disabledPlayers = new HashSet<>();
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {
        this.disabledPlayers.clear();
    }

    /**
     * Toggles a player's chopping status
     *
     * @param player The player to toggle
     * @return True if the player has chopping enabled, or false if they have it disabled
     */
    public boolean togglePlayer(Player player) {
        if (this.disabledPlayers.contains(player.getUniqueId())) {
            this.disabledPlayers.remove(player.getUniqueId());
            return true;
        } else {
            this.disabledPlayers.add(player.getUniqueId());
            return false;
        }
    }

    /**
     * Checks if a player has chopping enabled
     *
     * @param player The player to check
     * @return True if the player has chopping enabled, or false if they have it disabled
     */
    public boolean isChopping(Player player) {
        return !this.disabledPlayers.contains(player.getUniqueId());
    }

}
