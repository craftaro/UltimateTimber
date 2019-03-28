package com.songoda.ultimatetimber.hooks;

import com.songoda.ultimatetimber.tree.TreeBlock;
import org.bukkit.entity.Player;

import java.util.Set;

public interface TimberHook {
    
    /**
     * Applies the hook
     */
    void apply(Player player, Set<TreeBlock> treeBlocks) throws Exception;
    
}
