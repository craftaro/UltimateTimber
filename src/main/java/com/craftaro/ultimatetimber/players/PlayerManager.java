package com.craftaro.ultimatetimber.players;

import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.core.database.Data;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public class PlayerManager {

    private final UltimateTimber plugin;
    private final Map<UUID, TimberPlayer> playerStorage = new HashMap<>();
    public PlayerManager(UltimateTimber plugin) {
        this.plugin = plugin;
    }

    /**
     * Toggles a player's chopping status
     *
     * @param player The player to toggle
     * @return True if the player has chopping enabled, or false if they have it disabled
     */
    public boolean togglePlayer(Player player) {
        TimberPlayer timberPlayer = playerStorage.get(player.getUniqueId());
        if (timberPlayer == null) {
            return false;
        }

        timberPlayer.setChoppingEnabled(!timberPlayer.isChoppingEnabled());
        return timberPlayer.isChoppingEnabled();
    }

    /**
     * Sets a player into cooldown
     *
     * @param player The player to cooldown
     */
    public void cooldownPlayer(Player player) {
        int cooldownTime = plugin.getMainConfig().getInt("Settings.Tree Topple Cooldown");
        if (cooldownTime <= 0) {
            return;
        }

        if (player.hasPermission("ultimatetimber.bypasscooldown")) {
            return;
        }

        TimberPlayer timberPlayer = playerStorage.get(player.getUniqueId());
        if (timberPlayer == null) {
            return;
        }

        timberPlayer.setLastChoppingUse(Instant.now().getEpochSecond() + cooldownTime);
    }

    /**
     * Checks if a player is in cooldown and if they can topple trees
     *
     * @param player The player to check
     * @return True if the player can topple trees, otherwise false
     */
    public boolean canTopple(Player player) {
        TimberPlayer timberPlayer = playerStorage.get(player.getUniqueId());
        if (timberPlayer == null) {
            return false;
        }

        if (!timberPlayer.isChoppingEnabled()) {
            return false;
        }

        int cooldownTime = plugin.getMainConfig().getInt("Settings.Tree Topple Cooldown");
        if (cooldownTime <= 0) {
            return true;
        }

        if (timberPlayer.getLastChoppingUse() >= Instant.now().getEpochSecond()) {
            plugin.getLocaleManager().getMessage("event.onCooldown").sendMessage(player);
            return false;
        }

        return true;
    }

    // Boring database stuff
    /**
     * Load the player into cache.
     * @param player Player to load.
     */
    public void loadPlayer(Player player) {
        getPlayer(player, timberPlayer -> playerStorage.put(player.getUniqueId(), timberPlayer));
    }

    public void savePlayer(Player player) {
        TimberPlayer timberPlayer = playerStorage.remove(player.getUniqueId());
        if (timberPlayer == null) {
            return;
        }

        plugin.getDataManager().save(timberPlayer);
    }

    public void saveAllPlayers() {
        Collection<Data> dataToSave = new HashSet<>(playerStorage.values());
        plugin.getDataManager().saveBatchSync(dataToSave);
    }

    private void getPlayer(Player player, Consumer<TimberPlayer> callback) {
        if (playerStorage.containsKey(player.getUniqueId())) {
            callback.accept(playerStorage.get(player.getUniqueId()));
            return;
        }

        plugin.newChain().asyncFirst(() -> {
                    TimberPlayer timberPlayer = plugin.getDataManager().load(player.getUniqueId(), TimberPlayer.class, "players");
                    if (timberPlayer == null) {
                        timberPlayer = new TimberPlayer();
                        timberPlayer.setPlayer(player);
                        timberPlayer.setChoppingEnabled(true);
                        timberPlayer.setLastChoppingUse(0);
                    }

                    return timberPlayer;
                }).abortIfNull()
                .syncLast(callback::accept)
                .execute();
    }

}
