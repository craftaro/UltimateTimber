package com.craftaro.ultimatetimber.managers;

import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.ultimatetimber.events.TreePostFallEvent;
import com.craftaro.ultimatetimber.tree.ITreeBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.*;

public class PlacedBlockManager implements Listener {

    private final UltimateTimber plugin;
    private final Set<Location> placedBlocks;
    public PlacedBlockManager(UltimateTimber plugin) {
        this.plugin = plugin;
        this.placedBlocks = Collections.newSetFromMap(new LinkedHashMap<Location, Boolean>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Location, Boolean> eldest) {
                return this.size() > plugin.getMainConfig().getInt("Settings.Ignore Placed Blocks Memory Size");
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaced(BlockPlaceEvent event) {
        if (isDisabled()) {
            return;
        }

        // Ignore stripping logs
        if (event.getBlockPlaced().getType().name().contains("STRIPPED") && !event.getBlockReplacedState().getType().equals(Material.AIR))
            return;

        this.internalProtect(event.getBlock(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (isDisabled()) {
            return;
        }

        this.internalProtect(event.getBlock(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeafDecay(LeavesDecayEvent event) {
        if (isDisabled()) {
            return;
        }

        this.internalProtect(event.getBlock(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        if (isDisabled())
            return;

        for (BlockState blockState : event.getBlocks()) {
            this.internalProtect(blockState.getBlock(), false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTreeFell(TreePostFallEvent event) {
        if (isDisabled()) {
            return;
        }

        for (ITreeBlock<Block> treeBlock : event.getDetectedTree().getDetectedTreeBlocks().getAllTreeBlocks()) {
            this.internalProtect(treeBlock.getBlock(), false);
        }
    }

    /**
     * Handles when a block is placed/broken
     */
    private void internalProtect(Block block, boolean isPlaced) {
        if (isPlaced) {
            if (this.isBlockPlaced(block))
                return;

            this.placedBlocks.add(block.getLocation());
        } else {
            this.placedBlocks.remove(block.getLocation());
        }
    }

    /**
     * Gets if a block is placed
     *
     * @param block The Block to check
     * @return True if the block is placed, otherwise false
     */
    public boolean isBlockPlaced(Block block) {
        return this.placedBlocks.contains(block.getLocation());
    }

    public boolean isDisabled() {
        return plugin.getMainConfig().getInt("Settings.Ignore Placed Blocks Memory Size") <= 0;
    }
}
