package com.songoda.ultimatetimber.adapter.legacy.hooks;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.songoda.ultimatetimber.hook.TimberHook;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LegacyJobsHook implements TimberHook {

    @Override
    public void apply(Player player, TreeBlockSet<Block> treeBlocks) throws Exception {
        if (player.getGameMode().equals(GameMode.CREATIVE)) 
            return;
        
        // Replicate the same code that Jobs Reborn uses
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jPlayer == null) 
            return;
        
        for (ITreeBlock<Block> treeBlock : treeBlocks.getLogBlocks()) {
            Block block = treeBlock.getBlock();
            BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.BREAK);
            Jobs.action(jPlayer, bInfo, block);
        }
    }

}
