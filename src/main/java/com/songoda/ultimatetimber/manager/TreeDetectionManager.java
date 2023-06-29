package com.songoda.ultimatetimber.manager;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.tree.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.*;

public class TreeDetectionManager extends Manager {

    private final Set<Vector> VALID_TRUNK_OFFSETS, VALID_BRANCH_OFFSETS, VALID_LEAF_OFFSETS;

    private TreeDefinitionManager treeDefinitionManager;
    private PlacedBlockManager placedBlockManager;
    private int numLeavesRequiredForTree;
    private boolean onlyBreakLogsUpwards, entireTreeBase, destroyLeaves;

    public TreeDetectionManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);

        this.VALID_BRANCH_OFFSETS = new HashSet<>();
        this.VALID_TRUNK_OFFSETS = new HashSet<>();
        this.VALID_LEAF_OFFSETS = new HashSet<>();

        // 3x2x3 centered around log, excluding -y axis
        for (int y = 0; y <= 1; y++)
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    this.VALID_BRANCH_OFFSETS.add(new Vector(x, y, z));

        // 3x3x3 centered around log
        for (int y = -1; y <= 1; y++)
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    this.VALID_TRUNK_OFFSETS.add(new Vector(x, y, z));

        // Adjacent blocks to log
        for (int i = -1; i <= 1; i += 2) {
            this.VALID_LEAF_OFFSETS.add(new Vector(i, 0, 0));
            this.VALID_LEAF_OFFSETS.add(new Vector(0, i, 0));
            this.VALID_LEAF_OFFSETS.add(new Vector(0, 0, i));
        }
    }

    @Override
    public void reload() {
        this.treeDefinitionManager = this.plugin.getTreeDefinitionManager();
        this.placedBlockManager = this.plugin.getPlacedBlockManager();
        this.numLeavesRequiredForTree = ConfigurationManager.Setting.LEAVES_REQUIRED_FOR_TREE.getInt();
        this.onlyBreakLogsUpwards = ConfigurationManager.Setting.ONLY_DETECT_LOGS_UPWARDS.getBoolean();
        this.entireTreeBase = ConfigurationManager.Setting.BREAK_ENTIRE_TREE_BASE.getBoolean();
        this.destroyLeaves = ConfigurationManager.Setting.DESTROY_LEAVES.getBoolean();
    }

    @Override
    public void disable() {

    }

    /**
     * Detects a tree given an initial starting block
     *
     * @param initialBlock The starting Block of the detection
     * @return A DetectedTree if one was found, otherwise null
     */
    public DetectedTree detectTree(Block initialBlock) {
        TreeDefinitionManager treeDefinitionManager = this.plugin.getTreeDefinitionManager();

        TreeBlock initialTreeBlock = new TreeBlock(initialBlock, TreeBlockType.LOG);
        TreeBlockSet<Block> detectedTreeBlocks = new TreeBlockSet<>(initialTreeBlock);
        Set<TreeDefinition> possibleTreeDefinitions = this.treeDefinitionManager.getTreeDefinitionsForLog(initialBlock);

        if (possibleTreeDefinitions.isEmpty())
            return null;

        // Detect tree trunk
        List<Block> trunkBlocks = new ArrayList<>();
        trunkBlocks.add(initialBlock);
        Block targetBlock = initialBlock;
        while (this.isValidLogType(possibleTreeDefinitions, null, (targetBlock = targetBlock.getRelative(BlockFace.UP)))) {
            trunkBlocks.add(targetBlock);
            possibleTreeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(possibleTreeDefinitions, targetBlock, TreeBlockType.LOG));
        }

        if (!this.onlyBreakLogsUpwards) {
            targetBlock = initialBlock;
            while (this.isValidLogType(possibleTreeDefinitions, null, (targetBlock = targetBlock.getRelative(BlockFace.DOWN)))) {
                trunkBlocks.add(targetBlock);
                possibleTreeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(possibleTreeDefinitions, targetBlock, TreeBlockType.LOG));
            }
        }

        // Lowest blocks at the front of the list
        Collections.reverse(trunkBlocks);

        // Detect branches off the main trunk
        for (Block trunkBlock : trunkBlocks)
            this.recursiveBranchSearch(possibleTreeDefinitions, trunkBlocks, detectedTreeBlocks, trunkBlock, initialBlock.getLocation().getBlockY());

        // Detect leaves off the trunk/branches
        Set<ITreeBlock<Block>> branchBlocks = new HashSet<>(detectedTreeBlocks.getLogBlocks());
        for (ITreeBlock<Block> branchBlock : branchBlocks)
            this.recursiveLeafSearch(possibleTreeDefinitions, detectedTreeBlocks, branchBlock.getBlock(), new HashSet<>());

        // Use the first tree definition in the set
        TreeDefinition actualTreeDefinition = possibleTreeDefinitions.iterator().next();

        // Trees need at least a certain number of leaves
        if (detectedTreeBlocks.getLeafBlocks().size() < this.numLeavesRequiredForTree)
            return null;

        // Remove leaves if we don't care about the leaves
        if (!this.destroyLeaves)
            detectedTreeBlocks.removeAll(TreeBlockType.LEAF);

        // Check that the tree isn't on the ground if enabled
        if (this.entireTreeBase) {
            Set<Block> groundBlocks = new HashSet<>();
            for (ITreeBlock<Block> treeBlock : detectedTreeBlocks.getLogBlocks())
                if (treeBlock != detectedTreeBlocks.getInitialLogBlock() && treeBlock.getLocation().getBlockY() == initialBlock.getLocation().getBlockY())
                    groundBlocks.add(treeBlock.getBlock());

            for (Block block : groundBlocks) {
                Block blockBelow = block.getRelative(BlockFace.DOWN);
                boolean blockBelowIsLog = this.isValidLogType(possibleTreeDefinitions, null, blockBelow);
                boolean blockBelowIsSoil = false;
                for (CompatibleMaterial material : treeDefinitionManager.getPlantableSoilMaterial(actualTreeDefinition)) {
                    if (material.equals(CompatibleMaterial.getMaterial(blockBelow))) {
                        blockBelowIsSoil = true;
                        break;
                    }
                }

                if (blockBelowIsLog || blockBelowIsSoil)
                    return null;
            }
        }

        return new DetectedTree(actualTreeDefinition, detectedTreeBlocks);
    }

    /**
     * Recursively searches for branches off a given block
     *
     * @param treeDefinitions The possible tree definitions
     * @param trunkBlocks     The tree trunk blocks
     * @param treeBlocks      The detected tree blocks
     * @param block           The next block to check for a branch
     * @param startingBlockY  The Y coordinate of the initial block
     */
    private void recursiveBranchSearch(Set<TreeDefinition> treeDefinitions, List<Block> trunkBlocks, TreeBlockSet<Block> treeBlocks, Block block, int startingBlockY) {
        for (Vector offset : this.onlyBreakLogsUpwards ? this.VALID_BRANCH_OFFSETS : this.VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            TreeBlock treeBlock = new TreeBlock(targetBlock, TreeBlockType.LOG);
            if (this.isValidLogType(treeDefinitions, trunkBlocks, targetBlock) && !treeBlocks.contains(treeBlock)) {
                treeBlocks.add(treeBlock);
                treeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(treeDefinitions, targetBlock, TreeBlockType.LOG));
                if (!this.onlyBreakLogsUpwards || targetBlock.getLocation().getBlockY() > startingBlockY)
                    this.recursiveBranchSearch(treeDefinitions, trunkBlocks, treeBlocks, targetBlock, startingBlockY);
            }
        }
    }

    /**
     * Recursively searches for leaves that are next to this tree
     *
     * @param treeDefinitions The possible tree definitions
     * @param treeBlocks      The detected tree blocks
     * @param block           The next block to check for a leaf
     */
    private void recursiveLeafSearch(Set<TreeDefinition> treeDefinitions, TreeBlockSet<Block> treeBlocks, Block block, Set<Block> visitedBlocks) {
        boolean detectLeavesDiagonally = treeDefinitions.stream().anyMatch(TreeDefinition::shouldDetectLeavesDiagonally);

        for (Vector offset : !detectLeavesDiagonally ? this.VALID_LEAF_OFFSETS : this.VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            if (visitedBlocks.contains(targetBlock))
                continue;

            visitedBlocks.add(targetBlock);
            TreeBlock treeBlock = new TreeBlock(targetBlock, TreeBlockType.LEAF);
            if (this.isValidLeafType(treeDefinitions, treeBlocks, targetBlock) && !treeBlocks.contains(treeBlock) && !this.doesLeafBorderInvalidLog(treeDefinitions, treeBlocks, targetBlock)) {
                treeBlocks.add(treeBlock);
                treeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(treeDefinitions, targetBlock, TreeBlockType.LEAF));
                this.recursiveLeafSearch(treeDefinitions, treeBlocks, targetBlock, visitedBlocks);
            }
        }
    }

    /**
     * Checks if a leaf is bordering a log that isn't part of this tree
     *
     * @param treeDefinitions The possible tree definitions
     * @param treeBlocks      The detected tree blocks
     * @param block           The block to check
     * @return True if the leaf borders an invalid log, otherwise false
     */
    private boolean doesLeafBorderInvalidLog(Set<TreeDefinition> treeDefinitions, TreeBlockSet<Block> treeBlocks, Block block) {
        for (Vector offset : this.VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            if (this.isValidLogType(treeDefinitions, null, targetBlock) && !treeBlocks.contains(new TreeBlock(targetBlock, TreeBlockType.LOG)))
                return true;
        }
        return false;
    }

    /**
     * Checks if a given block is valid for the given TreeDefinitions
     *
     * @param treeDefinitions The Set of TreeDefinitions to compare against
     * @param trunkBlocks     The trunk blocks of the tree for checking the distance
     * @param block           The Block to check
     * @return True if the block is a valid log type, otherwise false
     */
    private boolean isValidLogType(Set<TreeDefinition> treeDefinitions, List<Block> trunkBlocks, Block block) {
        // Check if block is placed
        if (this.placedBlockManager.isBlockPlaced(block))
            return false;

        // Check if it matches the tree definition
        boolean isCorrectType = false;
        for (TreeDefinition treeDefinition : treeDefinitions) {
            for (CompatibleMaterial material : treeDefinition.getLogMaterial()) {
                if (material.equals(CompatibleMaterial.getMaterial(block))) {
                    isCorrectType = true;
                    break;
                }
            }
        }

        if (!isCorrectType)
            return false;

        // Check that it is close enough to the trunk
        if (trunkBlocks == null || trunkBlocks.isEmpty())
            return true;

        Location location = block.getLocation();
        for (TreeDefinition treeDefinition : treeDefinitions) {
            double maxDistance = treeDefinition.getMaxLogDistanceFromTrunk() * treeDefinition.getMaxLogDistanceFromTrunk();
            if (!this.onlyBreakLogsUpwards) // Help detect logs more often if the tree isn't broken at the base
                maxDistance *= 1.5;
            for (Block trunkBlock : trunkBlocks)
                if (location.distanceSquared(trunkBlock.getLocation()) < maxDistance)
                    return true;
        }

        return false;
    }

    /**
     * Checks if a given block is valid for the given TreeDefinitions
     *
     * @param treeDefinitions The Set of TreeDefinitions to compare against
     * @param treeBlocks      The detected blocks of the tree for checking leaf distance
     * @param block           The Block to check
     * @return True if the block is a valid log type, otherwise false
     */
    private boolean isValidLeafType(Set<TreeDefinition> treeDefinitions, TreeBlockSet<Block> treeBlocks, Block block) {
        // Check if block is placed
        if (this.placedBlockManager.isBlockPlaced(block))
            return false;

        // Check if it matches the tree definition
        boolean isCorrectType = false;
        for (TreeDefinition treeDefinition : treeDefinitions) {
            for (CompatibleMaterial material : treeDefinition.getLeafMaterial()) {
                if (material.equals(CompatibleMaterial.getMaterial(block))) {
                    isCorrectType = true;
                    break;
                }
            }
        }

        if (!isCorrectType)
            return false;

        // Check that it is close enough to a log
        if (treeBlocks == null || treeBlocks.isEmpty())
            return true;

        int maxDistanceFromLog = treeDefinitions.stream().map(TreeDefinition::getMaxLeafDistanceFromLog).max(Integer::compareTo).orElse(0);
        return treeBlocks.getLogBlocks().stream().anyMatch(x -> x.getLocation().distanceSquared(block.getLocation()) < maxDistanceFromLog * maxDistanceFromLog);
    }

}
