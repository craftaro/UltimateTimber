package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.adapter.IBlockData;
import com.songoda.ultimatetimber.tree.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class TreeDetectionManager extends Manager {

    private final Set<Vector> VALID_TRUNK_OFFSETS, VALID_BRANCH_OFFSETS, VALID_LEAF_OFFSETS;

    private TreeDefinitionManager treeDefinitionManager;
    private int maxBranchBlocksAllowed;
    private int numLeavesRequiredForTree;
    private boolean onlyBreakLogsUpwards;
    private boolean destroyBaseLog;
    private boolean entireTreeBase;

    public TreeDetectionManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);

        this.VALID_BRANCH_OFFSETS = new HashSet<>();
        this.VALID_TRUNK_OFFSETS = new HashSet<>();
        this.VALID_LEAF_OFFSETS = new HashSet<>();

        // 3x2x3 centered around log, excluding -y axis
        for (int x = -1; x <= 1; x++)
            for (int y = 0; y <= 1; y++)
                for (int z = -1; z <= 1; z++)
                    this.VALID_BRANCH_OFFSETS.add(new Vector(x, y, z));

        // 3x3x3 centered around log
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
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
        this.treeDefinitionManager = this.ultimateTimber.getTreeDefinitionManager();
        this.maxBranchBlocksAllowed = ConfigurationManager.Setting.MAX_LOGS_PER_CHOP.getInt();
        this.numLeavesRequiredForTree = ConfigurationManager.Setting.LEAVES_REQUIRED_FOR_TREE.getInt();
        this.onlyBreakLogsUpwards = ConfigurationManager.Setting.ONLY_DETECT_LOGS_UPWARDS.getBoolean();
        this.destroyBaseLog = ConfigurationManager.Setting.DESTROY_INITIATED_BLOCK.getBoolean();
        this.entireTreeBase = ConfigurationManager.Setting.BREAK_ENTIRE_TREE_BASE.getBoolean();
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
        TreeBlock initialTreeBlock = new TreeBlock(initialBlock, TreeBlockType.LOG);
        TreeBlockSet<Block> detectedTreeBlocks = new TreeBlockSet<>(initialTreeBlock);
        Set<TreeDefinition> possibleTreeDefinitions = this.treeDefinitionManager.getTreeDefinitionsForLog(initialBlock);

        if (possibleTreeDefinitions.isEmpty())
            return null;

        // Detect tree trunk
        Set<Block> trunkBlocks = new HashSet<>();
        trunkBlocks.add(initialBlock);
        Block targetBlock = initialBlock;
        while (this.isValidLogType(possibleTreeDefinitions, (targetBlock = targetBlock.getRelative(BlockFace.UP)))) {
            TreeBlock treeBlock = new TreeBlock(targetBlock, TreeBlockType.LOG);
            detectedTreeBlocks.add(treeBlock);
            trunkBlocks.add(initialBlock);
            possibleTreeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(possibleTreeDefinitions, targetBlock, TreeBlockType.LOG));
        }

        // Tree must be at least 2 blocks tall
        if (detectedTreeBlocks.size() < 2)
            return null;

        // Detect branches off the main trunk
        for (Block trunkBlock : trunkBlocks)
            this.recursiveBranchSearch(possibleTreeDefinitions, detectedTreeBlocks, trunkBlock, initialBlock.getY());

        // Detect leaves off the trunk/branches
        Set<ITreeBlock<Block>> branchBlocks = new HashSet<>(detectedTreeBlocks.getLogBlocks());
        for (ITreeBlock<Block> branchBlock : branchBlocks)
            this.recursiveLeafSearch(possibleTreeDefinitions, detectedTreeBlocks, branchBlock.getBlock(), 1);

        TreeDefinition actualTreeDefinition = possibleTreeDefinitions.iterator().next();

        // Trees need at least a certain number of leaves
        if (detectedTreeBlocks.getLeafBlocks().size() < this.numLeavesRequiredForTree)
            return null;

        // Check that the tree isn't on the ground if enabled
        if (this.entireTreeBase) {
            Set<Block> groundBlocks = new HashSet<>();
            for (ITreeBlock<Block> treeBlock : detectedTreeBlocks.getLogBlocks())
                if (treeBlock.getLocation().getBlockY() == initialBlock.getLocation().getBlockY())
                    groundBlocks.add(treeBlock.getBlock());

            for (Block block : groundBlocks) {
                Block blockBelow = block.getRelative(BlockFace.DOWN);
                boolean blockBelowIsLog = this.isValidLogType(possibleTreeDefinitions, blockBelow);
                boolean blockBelowIsSoil = false;
                for (IBlockData blockData : actualTreeDefinition.getPlantableSoilBlockData()) {
                    if (blockData.isSimilar(blockBelow)) {
                        blockBelowIsSoil = true;
                        break;
                    }
                }

                if (!blockBelowIsLog && blockBelowIsSoil)
                    return null;
            }
        }

        // Delete the starting block if applicable
        if (this.destroyBaseLog)
            detectedTreeBlocks.remove(initialTreeBlock);

        // Use the first tree definition in the set
        return new DetectedTree(actualTreeDefinition, detectedTreeBlocks);
    }

    /**
     * Recursively searches for branches off a given block
     *
     * @param treeDefinitions The possible tree definitions
     * @param treeBlocks The detected tree blocks
     * @param block The next block to check for a branch
     * @param startingBlockY The Y coordinate of the initial block
     */
    private void recursiveBranchSearch(Set<TreeDefinition> treeDefinitions, TreeBlockSet<Block> treeBlocks, Block block, int startingBlockY) {
        if (treeBlocks.size() > this.maxBranchBlocksAllowed)
            return;

        for (Vector offset : this.onlyBreakLogsUpwards ? this.VALID_BRANCH_OFFSETS : this.VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            TreeBlock treeBlock = new TreeBlock(targetBlock, TreeBlockType.LOG);
            if (this.isValidLogType(treeDefinitions, targetBlock) && !treeBlocks.contains(treeBlock)) {
                treeBlocks.add(treeBlock);
                treeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(treeDefinitions, block, TreeBlockType.LOG));
                if (!this.onlyBreakLogsUpwards || targetBlock.getLocation().getBlockY() > startingBlockY)
                    this.recursiveBranchSearch(treeDefinitions, treeBlocks, targetBlock, startingBlockY);
            }
        }
    }

    /**
     * Recursively searches for leaves that are next to this tree
     *
     * @param treeDefinitions The possible tree definitions
     * @param treeBlocks The detected tree blocks
     * @param block The next block to check for a leaf
     * @param distanceFromLog The distance this leaf is from a log
     */
    private void recursiveLeafSearch(Set<TreeDefinition> treeDefinitions, TreeBlockSet<Block> treeBlocks, Block block, int distanceFromLog) {
        int maxDistanceFromLog = treeDefinitions.stream().max(Comparator.comparingInt(TreeDefinition::getMaxLeafDistanceFromLog)).get().getMaxLeafDistanceFromLog();
        boolean detectLeavesDiagonally = treeDefinitions.stream().anyMatch(TreeDefinition::shouldDetectLeavesDiagonally);

        if (distanceFromLog > maxDistanceFromLog)
            return;

        for (Vector offset : !detectLeavesDiagonally ? this.VALID_LEAF_OFFSETS : this.VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            TreeBlock treeBlock = new TreeBlock(targetBlock, TreeBlockType.LEAF);
            if (this.isValidLeafType(treeDefinitions, targetBlock)) {
                if (!treeBlocks.contains(treeBlock) && !this.doesLeafBorderInvalidLog(treeDefinitions, treeBlocks, targetBlock)) {
                    treeBlocks.add(treeBlock);
                    treeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(treeDefinitions, block, TreeBlockType.LEAF));
                }
                this.recursiveLeafSearch(treeDefinitions, treeBlocks, targetBlock, distanceFromLog + 1);
            }
        }
    }

    /**
     * Checks if a leaf is bordering a log that isn't part of this tree
     *
     * @param treeDefinitions The possible tree definitions
     * @param treeBlocks The detected tree blocks
     * @param block The block to check
     * @return True if the leaf borders an invalid log, otherwise false
     */
    private boolean doesLeafBorderInvalidLog(Set<TreeDefinition> treeDefinitions, TreeBlockSet<Block> treeBlocks, Block block) {
        for (Vector offset : this.VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            if (this.isValidLogType(treeDefinitions, targetBlock) && !treeBlocks.contains(new TreeBlock(targetBlock, TreeBlockType.LOG)))
                return true;
        }
        return false;
    }

    /**
     * Checks if a given block is valid for the given TreeDefinitions
     *
     * @param treeDefinitions The Set of TreeDefinitions to compare against
     * @param block The Block to check
     * @return True if the block is a valid log type, otherwise false
     */
    private boolean isValidLogType(Set<TreeDefinition> treeDefinitions, Block block) {
        for (TreeDefinition treeDefinition : treeDefinitions)
            for (IBlockData logBlockData : treeDefinition.getLogBlockData())
                if (logBlockData.isSimilar(block))
                    return true;
        return false;
    }

    /**
     * Checks if a given block is valid for the given TreeDefinitions
     *
     * @param treeDefinitions The Set of TreeDefinitions to compare against
     * @param block The Block to check
     * @return True if the block is a valid log type, otherwise false
     */
    private boolean isValidLeafType(Set<TreeDefinition> treeDefinitions, Block block) {
        for (TreeDefinition treeDefinition : treeDefinitions)
            for (IBlockData leafBlockData : treeDefinition.getLeafBlockData())
                if (leafBlockData.isSimilar(block))
                    return true;
        return false;
    }

}
