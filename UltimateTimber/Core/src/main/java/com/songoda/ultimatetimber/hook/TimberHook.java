package com.songoda.ultimatetimber.hook;

import com.songoda.ultimatetimber.tree.TreeBlockSet;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface TimberHook {

    /**
     * Applies experience to a player for a fallen tree
     *
     * @param player The player
     * @param treeBlocks The tree blocks that were broken
     */
    void applyExperience(Player player, TreeBlockSet<Block> treeBlocks);

    /**
     * Checks if double drops should be applied
     *
     * @param player The player
     * @return True if double drops should be applied, otherwise false
     */
    boolean shouldApplyDoubleDrops(Player player);

    /**
     * Checks if a player is using an ability
     *
     * @param player The player
     * @return True if an ability is being used, otherwise false
     */
    boolean isUsingAbility(Player player);
    
}
