package com.songoda.ultimatetimber.treefall;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import com.songoda.ultimatetimber.utils.LogToLeafConverter;

public class TreeChecker {

    /*
    Used to check if a piece of wood is a part of the tree
     */
    private final static Set<Material> VALID_LOG_MATERIALS = new HashSet<>(Arrays.asList(
            Material.ACACIA_LOG,
            Material.ACACIA_WOOD,
            Material.STRIPPED_ACACIA_LOG,
            Material.STRIPPED_ACACIA_WOOD,
            Material.BIRCH_LOG,
            Material.BIRCH_WOOD,
            Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_BIRCH_WOOD,
            Material.DARK_OAK_LOG,
            Material.DARK_OAK_WOOD,
            Material.STRIPPED_DARK_OAK_LOG,
            Material.STRIPPED_DARK_OAK_WOOD,
            Material.JUNGLE_LOG,
            Material.JUNGLE_WOOD,
            Material.STRIPPED_JUNGLE_LOG,
            Material.STRIPPED_JUNGLE_WOOD,
            Material.OAK_LOG,
            Material.OAK_WOOD,
            Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_OAK_WOOD,
            Material.SPRUCE_LOG,
            Material.SPRUCE_WOOD,
            Material.STRIPPED_SPRUCE_LOG,
            Material.STRIPPED_SPRUCE_WOOD,
            Material.MUSHROOM_STEM
    ));
    
    /*
    Used to check if a leaf is a part of the tree
     */
    private final static Set<Material> VALID_LEAF_MATERIALS = new HashSet<>(Arrays.asList(
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES,
            Material.BROWN_MUSHROOM_BLOCK,
            Material.RED_MUSHROOM_BLOCK
    ));
    
    /**
     * Gets a Set of all valid wood materials
     * 
     * @return A Set of all valid wood materials
     */
    public static Set<Material> getValidWoodMaterials() {
        return VALID_LOG_MATERIALS;
    }
    
    private static final Set<Vector> VALID_TRUNK_OFFSETS, VALID_BRANCH_OFFSETS, VALID_LEAF_OFFSETS;
    
    private HashSet<Block> treeBlocks;
    private int maxDistanceFromLog;
    private Material logType, leafType;
    private int startingBlockY;
    private int maxBranchBlocksAllowed;
    private int numLeavesRequiredForTree;
    private boolean allowMixedTreeTypes;
    private boolean onlyBreakLogsUpwards;
    private boolean destroyBaseLog;
    private boolean entireTreeBase;
    private boolean isMushroom = false;
    
    static {
        VALID_BRANCH_OFFSETS = new HashSet<>();
        VALID_TRUNK_OFFSETS = new HashSet<>();
        VALID_LEAF_OFFSETS = new HashSet<>();
        
        // 3x2x3 centered around log, excluding -y axis
        for (int x = -1; x <= 1; x++)
            for (int y = 0; y <= 1; y++)
                for (int z = -1; z <= 1; z++)
                    VALID_BRANCH_OFFSETS.add(new Vector(x, y, z));
        
        // 3x3x3 centered around log
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++)
                    VALID_TRUNK_OFFSETS.add(new Vector(x, y, z));
        
        // Adjacent blocks to log
        for (int i = -1; i <= 1; i += 2) {
            VALID_LEAF_OFFSETS.add(new Vector(i, 0, 0));
            VALID_LEAF_OFFSETS.add(new Vector(0, i, 0));
            VALID_LEAF_OFFSETS.add(new Vector(0, 0, i));
        }
    }

    /**
     * Parses a block for a potential tree
     * 
     * @param block The based block of the potential tree
     * @return A HashSet of all blocks in the tree, or null if no tree was found
     */
    protected HashSet<Block> parseTree(Block block) {
        this.treeBlocks = new HashSet<>();
        this.treeBlocks.add(block);
        
        // Set tree information
        this.logType = block.getType();
        this.leafType = LogToLeafConverter.convert(this.logType);
        this.startingBlockY = block.getLocation().getBlockY();
        this.isMushroom = this.logType.equals(Material.MUSHROOM_STEM);
        
        // Load settings for algorithm
        FileConfiguration config = UltimateTimber.getInstance().getConfig();
        this.allowMixedTreeTypes = config.getBoolean(DefaultConfig.ALLOW_MIXED_TREE_TYPES);
        this.maxBranchBlocksAllowed = config.getInt(DefaultConfig.MAX_BRANCH_BLOCKS);
        this.numLeavesRequiredForTree = config.getInt(DefaultConfig.LEAVES_FOR_TREE);
        this.onlyBreakLogsUpwards = config.getBoolean(DefaultConfig.ONLY_BREAK_LOGS_UPWARDS);
        this.destroyBaseLog = config.getBoolean(DefaultConfig.DELETE_BROKEN_LOG);
        this.entireTreeBase = config.getBoolean(DefaultConfig.ENTIRE_TREE_BASE);
        
        // Detect tree trunk
        Set<Block> trunkBlocks = new HashSet<>();
        trunkBlocks.add(block);
        Block targetBlock = block;
        while (this.isValidLogType((targetBlock = targetBlock.getRelative(BlockFace.UP)).getType())) {
            this.treeBlocks.add(targetBlock);
            trunkBlocks.add(targetBlock);
        }
        
        // Tree must be at least 2 blocks tall
        if (this.treeBlocks.size() < 2)
            return null;
        
        // Detect branches off the main trunk
        for (Block trunkBlock : trunkBlocks)
            this.recursiveBranchSearch(trunkBlock);
        
        // Make it so trees only break as many leaves as they have to
        this.maxDistanceFromLog = this.getMaxLeafDistanceFromLog();
        
        // Detect leaves off the trunk/branches
        Set<Block> branchBlocks = new HashSet<Block>(this.treeBlocks);
        for (Block branchBlock : branchBlocks)
            this.recursiveLeafSearch(branchBlock, 1);
        
        // Trees need at least 5 leaves
        if (!this.isMushroom && this.treeBlocks.stream().filter(x -> this.isValidLeafType(x.getType())).count() < this.numLeavesRequiredForTree)
            return null;
        
        // All logs must not have a plantable surface below them (if enabled)
        if (this.entireTreeBase) {
            boolean isTreeGrounded = this.treeBlocks.stream().anyMatch(x -> {
                Material typeBelow = x.getRelative(BlockFace.DOWN).getType();
                return (typeBelow.equals(Material.DIRT) || 
                        typeBelow.equals(Material.COARSE_DIRT) || 
                        typeBelow.equals(Material.PODZOL) || 
                        typeBelow.equals(Material.GRASS_BLOCK)) &&
                        !x.equals(block) &&
                        isValidLogType(x.getType());
            });
            if (isTreeGrounded)
                return null;
        }
        
        // Delete the starting block if applicable
        if (this.destroyBaseLog)
            this.treeBlocks.remove(block);
        
        return this.treeBlocks;
    }
    
    /**
     * Recursively searches for branches off a given block
     * 
     * @param block The next block to check for a branch
     */
    private void recursiveBranchSearch(Block block) {
        if (this.treeBlocks.size() > this.maxBranchBlocksAllowed)
            return;
        
        for (Vector offset : this.onlyBreakLogsUpwards ? VALID_BRANCH_OFFSETS : VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            if (this.isValidLogType(targetBlock.getType()) && !this.treeBlocks.contains(targetBlock)) {
                this.treeBlocks.add(targetBlock);
                if (!this.onlyBreakLogsUpwards || targetBlock.getLocation().getBlockY() > this.startingBlockY)
                    this.recursiveBranchSearch(targetBlock);
            }
        }
    }
    
    /**
     * Recursively searches for leaves that are next to this tree
     * 
     * @param block The next block to check for a leaf
     * @param distanceFromLog The distance this leaf is from a log
     */
    private void recursiveLeafSearch(Block block, int distanceFromLog) {
        if (distanceFromLog > this.maxDistanceFromLog)
            return;
        
        for (Vector offset : !this.isMushroom ? VALID_LEAF_OFFSETS : VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            if (this.isValidLeafType(targetBlock.getType()) || (this.isMushroom && this.isMushroomBlock(targetBlock.getType()))) {
                if (!this.treeBlocks.contains(targetBlock) && !doesLeafBorderInvalidLog(targetBlock))
                    this.treeBlocks.add(targetBlock);
                this.recursiveLeafSearch(targetBlock, distanceFromLog + 1);
            }
        }
    }
    
    /**
     * Checks if a leaf is bordering a log that isn't part of this tree
     * 
     * @param block The block to check
     * @return If the leaf borders an invalid log
     */
    private boolean doesLeafBorderInvalidLog(Block block) {
        for (Vector offset : VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            if (this.isValidLogType(targetBlock.getType()) && !this.treeBlocks.contains(targetBlock))
                return true;
        }
        return false;
    }
    
    private boolean isValidLogType(Material material) {
        if (this.allowMixedTreeTypes)
            return VALID_LOG_MATERIALS.contains(material);
        return material.equals(this.logType);
    }
    
    private boolean isValidLeafType(Material material) {
        if (this.allowMixedTreeTypes)
            return VALID_LEAF_MATERIALS.contains(material);
        return material.equals(this.leafType);
    }
    
    /**
     * Checks if a block is a mushroom head block
     * 
     * @param block The block to check
     * @return If the given block is a mushroom
     */
    private boolean isMushroomBlock(Material material) {
        return material.equals(Material.BROWN_MUSHROOM_BLOCK) || material.equals(Material.RED_MUSHROOM_BLOCK);
    }
    
    /**
     * Gets the max distance away from a log based on how many logs there are and the leaf type
     * 
     * @return The max distance away a leaf can be from a log
     */
    private int getMaxLeafDistanceFromLog() {
        int numLogs = this.treeBlocks.size();
        
        switch (this.leafType) {
        
        case ACACIA_LEAVES:
            return 5;
            
        case BIRCH_LEAVES: 
            return 4;
            
        case DARK_OAK_LEAVES:
            return 5;
            
        case JUNGLE_LEAVES:
            if (numLogs > 15)
                return 5;
            return 4;
            
        case OAK_LEAVES:
            if (numLogs > 15)
                return 6;
            if (numLogs > 6)
                return 5;
            return 4;
            
        case SPRUCE_LEAVES:
            if (numLogs > 15)
                return 6;
            return 5;
            
        case MUSHROOM_STEM:
            return 4;
        
        default:
            return -1;
        }
    }
    
    public HashSet<Block> getTreeBlocks() {
        return this.treeBlocks;
    }

}
