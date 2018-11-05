package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.configurations.DefaultConfig;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class TreeFallEvent implements Listener {

    /*
    This is the starting point for the whole effect
    It's been broken up instead of chained in order to make step-by-step debugging easier
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTreeBreak(BlockBreakEvent event) {

        if (!EventFilter.eventIsValid(event)) return;
        ArrayList<Block> blocks = TreeChecker.parseTree(event.getBlock());
        if (blocks == null) return;
        if (DefaultConfig.configuration.getBoolean(DefaultConfig.ACCURATE_AXE_DURABILITY))
            AxeDurability.adjustAxeDamage(blocks, event.getPlayer());
        if (DefaultConfig.configuration.getBoolean(DefaultConfig.CUSTOM_AUDIO))
            TreeSounds.tipOverNoise(event.getBlock().getLocation());
        TreeFallAnimation.startAnimation(event.getBlock(), blocks, event.getPlayer());

    }

}
