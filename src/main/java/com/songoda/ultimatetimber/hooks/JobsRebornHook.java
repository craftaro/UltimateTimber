package com.songoda.ultimatetimber.hooks;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.songoda.ultimatetimber.utils.WoodToLogConverter;

public class JobsRebornHook {

    private static boolean enabled = false;
    
    /**
     * Updates a Player's Woodcutter job based on the number of logs they broke
     * 
     * @param player The Player to update
     * @param treeBlocks The tree blocks that were broken
     */
    public static void updateWoodcutterJob(Player player, HashSet<Block> treeBlocks) {
        if (!enabled) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        
        try {
            // Replicate the same code that Jobs Reborn uses
            JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
            if (jPlayer == null) 
                return;
            
            for (Block log : getLogs(treeBlocks)) {
                BlockActionInfo bInfo = new BlockActionInfo(log, ActionType.BREAK);
                Jobs.action(jPlayer, bInfo, log);
            }
        } catch (Exception ex) {
            Bukkit.getLogger().warning("[UltimateTimber] Warning: The version of Jobs Reborn you are using is not compatible with UltimateTimber. Disabling hook...");
            enabled = false;
        }
    }
    
    /**
     * Sets the hook to enabled
     */
    public static boolean setEnabled() {
        enabled = true;
        return true;
    }
    
    /**
     * Separates out the logs from the rest of the tree blocks
     * 
     * @param treeBlocks The set of blocks in the tree that was broken
     * @return A set of the logs in the tree blocks
     */
    private static Set<Block> getLogs(HashSet<Block> treeBlocks) {
        return treeBlocks.stream().filter(x -> WoodToLogConverter.convert(x.getType()).name().endsWith("LOG")).collect(Collectors.toSet());
    }

}
