package com.craftaro.ultimatetimber.managers;

import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.ultimatetimber.tree.*;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.*;

public class TreeDetectionManager {

    private final UltimateTimber plugin;
    private final Set<Vector> validTrunkOffsets = new HashSet<>(),
            validBranchOffsets = new HashSet<>(),
            validLeafOffsets = new HashSet<>();
    public TreeDetectionManager(UltimateTimber plugin) {
        this.plugin = plugin;

        // 3x2x3 centered around log, excluding -y axis
        for (int y = 0; y <= 1; y++)
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    this.validBranchOffsets.add(new Vector(x, y, z));

        // 3x3x3 centered around log
        for (int y = -1; y <= 1; y++)
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    this.validTrunkOffsets.add(new Vector(x, y, z));

        // Adjacent blocks to log
        for (int i = -1; i <= 1; i += 2) {
            this.validLeafOffsets.add(new Vector(i, 0, 0));
            this.validLeafOffsets.add(new Vector(0, i, 0));
            this.validLeafOffsets.add(new Vector(0, 0, i));
        }
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
        Set<TreeDefinition> possibleTreeDefinitions = plugin.getTreeDefinitionManager().getTreeDefinitionsForLog(initialBlock);

        if (possibleTreeDefinitions.isEmpty()) {
            return null;
        }

        // Detect tree trunk
        List<Block> trunkBlocks = new ArrayList<>();
        trunkBlocks.add(initialBlock);
        Block targetBlock = initialBlock;
        while (this.isValidLogType(possibleTreeDefinitions, null, (targetBlock = targetBlock.getRelative(BlockFace.UP)))) {
            trunkBlocks.add(targetBlock);
            possibleTreeDefinitions.retainAll(plugin.getTreeDefinitionManager().narrowTreeDefinition(possibleTreeDefinitions, targetBlock, TreeBlockType.LOG));
        }

        if (!plugin.getMainConfig().getBoolean("Settings.Only Detect Logs Upwards")) {
            targetBlock = initialBlock;
            while (this.isValidLogType(possibleTreeDefinitions, null, (targetBlock = targetBlock.getRelative(BlockFace.DOWN)))) {
                trunkBlocks.add(targetBlock);
                possibleTreeDefinitions.retainAll(plugin.getTreeDefinitionManager().narrowTreeDefinition(possibleTreeDefinitions, targetBlock, TreeBlockType.LOG));
            }
        }

        // Lowest blocks at the front of the list
        Collections.reverse(trunkBlocks);

        // Detect branches off the main trunk
        for (Block trunkBlock : trunkBlocks) {
            this.recursiveBranchSearch(possibleTreeDefinitions, trunkBlocks, detectedTreeBlocks, trunkBlock, initialBlock.getLocation().getBlockY());
        }

        // Detect leaves off the trunk/branches
        Set<ITreeBlock<Block>> branchBlocks = new HashSet<>(detectedTreeBlocks.getLogBlocks());
        for (ITreeBlock<Block> branchBlock : branchBlocks) {
            this.recursiveLeafSearch(possibleTreeDefinitions, detectedTreeBlocks, branchBlock.getBlock(), new HashSet<>());
        }

        // Use the first tree definition in the set
        TreeDefinition actualTreeDefinition = possibleTreeDefinitions.iterator().next();

        // Trees need at least a certain number of leaves
        if (detectedTreeBlocks.getLeafBlocks().size() < plugin.getMainConfig().getInt("Settings.Leaves Required For Tree")) {
            return null;
        }

        // Remove leaves if we don't care about the leaves
        if (!plugin.getMainConfig().getBoolean("Settings.Destroy Leaves")) {
            detectedTreeBlocks.removeAll(TreeBlockType.LEAF);
        }

        // Check that the tree isn't on the ground if enabled
        if (plugin.getMainConfig().getBoolean("Settings.Break Entire Tree Base")) {
            Set<Block> groundBlocks = new HashSet<>();
            for (ITreeBlock<Block> treeBlock : detectedTreeBlocks.getLogBlocks())
                if (treeBlock != detectedTreeBlocks.getInitialLogBlock() && treeBlock.getLocation().getBlockY() == initialBlock.getLocation().getBlockY())
                    groundBlocks.add(treeBlock.getBlock());

            for (Block block : groundBlocks) {
                Block blockBelow = block.getRelative(BlockFace.DOWN);
                boolean blockBelowIsLog = this.isValidLogType(possibleTreeDefinitions, null, blockBelow);
                boolean blockBelowIsSoil = false;
                for (XMaterial material : treeDefinitionManager.getPlantableSoilMaterial(actualTreeDefinition)) {
                    if (material == XMaterial.matchXMaterial(blockBelow.getType())) {
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
        for (Vector offset : plugin.getMainConfig().getBoolean("Settings.Only Detect Logs Upwards") ? this.validBranchOffsets : this.validTrunkOffsets) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            TreeBlock treeBlock = new TreeBlock(targetBlock, TreeBlockType.LOG);
            if (this.isValidLogType(treeDefinitions, trunkBlocks, targetBlock) && !treeBlocks.contains(treeBlock)) {
                treeBlocks.add(treeBlock);
                treeDefinitions.retainAll(plugin.getTreeDefinitionManager().narrowTreeDefinition(treeDefinitions, targetBlock, TreeBlockType.LOG));
                if (!plugin.getMainConfig().getBoolean("Settings.Only Detect Logs Upwards") || targetBlock.getLocation().getBlockY() > startingBlockY)
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

        for (Vector offset : !detectLeavesDiagonally ? validLeafOffsets : validTrunkOffsets) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            if (visitedBlocks.contains(targetBlock))
                continue;

            visitedBlocks.add(targetBlock);
            TreeBlock treeBlock = new TreeBlock(targetBlock, TreeBlockType.LEAF);
            if (this.isValidLeafType(treeDefinitions, treeBlocks, targetBlock) && !treeBlocks.contains(treeBlock) && !this.doesLeafBorderInvalidLog(treeDefinitions, treeBlocks, targetBlock)) {
                treeBlocks.add(treeBlock);
                treeDefinitions.retainAll(plugin.getTreeDefinitionManager().narrowTreeDefinition(treeDefinitions, targetBlock, TreeBlockType.LEAF));
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
        for (Vector offset : validTrunkOffsets) {
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
        if (plugin.getPlacedBlockManager().isBlockPlaced(block))
            return false;

        // Check if it matches the tree definition
        boolean isCorrectType = false;
        for (TreeDefinition treeDefinition : treeDefinitions) {
            for (XMaterial material : treeDefinition.getLogMaterial()) {
                if (material == XMaterial.matchXMaterial(block.getType())) {
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
            if (!plugin.getMainConfig().getBoolean("Settings.Only Detect Logs Upwards")) // Help detect logs more often if the tree isn't broken at the base
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
        if (plugin.getPlacedBlockManager().isBlockPlaced(block))
            return false;

        // Check if it matches the tree definition
        boolean isCorrectType = false;
        for (TreeDefinition treeDefinition : treeDefinitions) {
            for (XMaterial material : treeDefinition.getLeafMaterial()) {
                if (material == XMaterial.matchXMaterial(block.getType())) {
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