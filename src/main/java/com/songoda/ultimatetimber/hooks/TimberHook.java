package com.songoda.ultimatetimber.hooks;

import com.songoda.ultimatetimber.tree.TreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

public interface TimberHook {
    
    /**
     * Applies the hook
     */
    void apply(Player player, TreeBlockSet<Block> treeBlocks) throws Exception;
    
}
