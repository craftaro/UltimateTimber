package com.craftaro.ultimatetimber.players;

import com.craftaro.core.database.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimberPlayer implements Data {

    private Player player;
    private boolean choppingEnabled;
    private long lastChoppingUse;

    public Player getPlayer() {
        return player;
    }

    public boolean isChoppingEnabled() {
        return choppingEnabled;
    }

    public long getLastChoppingUse() {
        return lastChoppingUse;
    }

    protected void setPlayer(Player player) {
        this.player = player;
    }

    public void setChoppingEnabled(boolean choppingEnabled) {
        this.choppingEnabled = choppingEnabled;
    }

    public void setLastChoppingUse(long lastChoppingUse) {
        this.lastChoppingUse = lastChoppingUse;
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializedData = new HashMap<>();
        serializedData.put("uuid", player.getUniqueId().toString());
        serializedData.put("chopping_enabled", choppingEnabled);
        serializedData.put("last_chopping_use", lastChoppingUse);

        return serializedData;
    }

    @Override
    public Data deserialize(Map<String, Object> map) {
        this.player = Bukkit.getPlayer(UUID.fromString((String)map.get("uuid")));
        this.choppingEnabled = (boolean)map.get("chopping_enabled");
        this.lastChoppingUse = (long)map.get("last_chopping_use");

        return this;
    }

    @Override
    public String getTableName() {
        return "players";
    }
}
