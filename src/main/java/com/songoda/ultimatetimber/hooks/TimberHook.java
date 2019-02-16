package com.songoda.ultimatetimber.hooks;

import java.util.HashSet;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface TimberHook {
    
    /**
     * Applies the hook
     */
    public void apply(Player player, HashSet<Block> treeBlocks) throws Exception;
    
}
