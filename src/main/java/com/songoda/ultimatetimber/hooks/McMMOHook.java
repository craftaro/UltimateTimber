package com.songoda.ultimatetimber.hooks;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import com.songoda.ultimatetimber.utils.WoodToLogConverter;

public class McMMOHook {

    private static boolean enabled = false;
    
    /**
     * Updates a Player's Wood Cutting skill based on the number of logs they broke
     * 
     * @param player The Player to update
     * @param treeBlocks The tree blocks that were broken
     */
    public static void updateWoodCuttingSkill(Player player, HashSet<Block> treeBlocks) {
        if (!enabled) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        
        ExperienceAPI.addXpFromBlocksBySkill(getLogStates(treeBlocks), UserManager.getPlayer(player), PrimarySkillType.WOODCUTTING);
    }
    
    /**
     * Sets the hook to enabled
     */
    public static void setEnabled() {
        enabled = true;
    }
    
    /**
     * Counts the number of logs
     * 
     * @param treeBlocks The potential log blocks
     * @return The number of logs
     */
    private static ArrayList<BlockState> getLogStates(HashSet<Block> treeBlocks) {
        ArrayList<BlockState> logs = new ArrayList<>();
        for (Block block : treeBlocks) {
            Material material = WoodToLogConverter.convert(block.getType());
            if (material.name().endsWith("LOG")) {
                logs.add(block.getState());
            }
        }
        return logs;
    }

}
