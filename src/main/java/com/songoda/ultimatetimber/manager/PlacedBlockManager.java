package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.events.TreeFellEvent;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import org.bukkit.Bukkit;
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PlacedBlockManager extends Manager implements Listener {

    private Set<Location> placedBlocks;
    private boolean ignorePlacedBlocks;
    private int maxPlacedBlockMemorySize;

    public PlacedBlockManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        Bukkit.getPluginManager().registerEvents(this, ultimateTimber);
    }

    @Override
    public void reload() {
        this.ignorePlacedBlocks = ConfigurationManager.Setting.IGNORE_PLACED_BLOCKS.getBoolean();
        this.maxPlacedBlockMemorySize = ConfigurationManager.Setting.IGNORE_PLACED_BLOCKS_MEMORY_SIZE.getInt();

        // Ensures the oldest entry is removed if it exceeds the limit
        this.placedBlocks = Collections.newSetFromMap(new LinkedHashMap<Location, Boolean>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Location, Boolean> eldest) {
                return this.size() > PlacedBlockManager.this.maxPlacedBlockMemorySize;
            }
        });
    }

    @Override
    public void disable() {
        this.placedBlocks.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaced(BlockPlaceEvent event) {
        if (!this.ignorePlacedBlocks)
            return;

        // Ignore stripping logs
        if (event.getBlockPlaced().getType().name().contains("STRIPPED") && !event.getBlockReplacedState().getType().equals(Material.AIR))
            return;

        this.internalProtect(event.getBlock(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!this.ignorePlacedBlocks)
            return;

        this.internalProtect(event.getBlock(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeafDecay(LeavesDecayEvent event) {
        if (!this.ignorePlacedBlocks)
            return;

        this.internalProtect(event.getBlock(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        if (!this.ignorePlacedBlocks)
            return;

        for (BlockState blockState : event.getBlocks())
            this.internalProtect(blockState.getBlock(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTreeFell(TreeFellEvent event) {
        if (!this.ignorePlacedBlocks)
            return;

        for (ITreeBlock<Block> treeBlock : event.getDetectedTree().getDetectedTreeBlocks().getAllTreeBlocks())
            this.internalProtect(treeBlock.getBlock(), false);
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

}
