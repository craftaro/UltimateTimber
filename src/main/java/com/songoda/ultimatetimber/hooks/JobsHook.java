package com.songoda.ultimatetimber.hooks;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.songoda.ultimatetimber.utils.WoodToLogConverter;

public class JobsHook implements TimberHook {

    @Override
    public void apply(Player player, HashSet<Block> treeBlocks) throws Exception {
        if (player.getGameMode().equals(GameMode.CREATIVE)) 
            return;
        
        // Replicate the same code that Jobs Reborn uses
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jPlayer == null) 
            return;
        
        Set<Block> logs = treeBlocks.stream().filter(x -> WoodToLogConverter.convert(x.getType()).name().endsWith("LOG")).collect(Collectors.toSet());
        for (Block log : logs) {
            BlockActionInfo bInfo = new BlockActionInfo(log, ActionType.BREAK);
            Jobs.action(jPlayer, bInfo, log);
        }
    }

}
