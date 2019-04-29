package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.adapter.IBlockData;
import com.songoda.ultimatetimber.events.TreeFellEvent;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockType;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PlacedBlockManager extends Manager implements Listener {

    private List<Location> placedBlocks;
    private boolean ignorePlacedBlocks;
    private int maxPlacedBlockMemorySize;

    public PlacedBlockManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        this.placedBlocks = new ArrayList<>();
        this.ignorePlacedBlocks = true;
        this.maxPlacedBlockMemorySize = 10000;
        Bukkit.getPluginManager().registerEvents(this, ultimateTimber);
    }

    @Override
    public void reload() {
        this.placedBlocks.clear();
        this.ignorePlacedBlocks = ConfigurationManager.Setting.IGNORE_PLACED_BLOCKS.getBoolean();
        this.maxPlacedBlockMemorySize = ConfigurationManager.Setting.IGNORE_PLACED_BLOCKS_MEMORY_SIZE.getInt();
    }

    @Override
    public void disable() {
        this.placedBlocks.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaced(BlockPlaceEvent event) {
        if (!this.ignorePlacedBlocks)
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
            if (this.placedBlocks.size() > this.maxPlacedBlockMemorySize)
                this.placedBlocks.remove(0);
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
