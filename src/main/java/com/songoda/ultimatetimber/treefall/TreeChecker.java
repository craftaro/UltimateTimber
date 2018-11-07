package com.songoda.ultimatetimber.treefall;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class TreeChecker {

    /*
    Used to check if a tree is a tree
     */
    public static List<Material> validMaterials = new ArrayList<>(Arrays.asList(
            Material.ACACIA_LOG,
            Material.STRIPPED_ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.STRIPPED_BIRCH_LOG,
            Material.DARK_OAK_LOG,
            Material.STRIPPED_DARK_OAK_LOG,
            Material.JUNGLE_LOG,
            Material.STRIPPED_JUNGLE_LOG,
            Material.OAK_LOG,
            Material.STRIPPED_OAK_LOG,
            Material.SPRUCE_LOG,
            Material.STRIPPED_SPRUCE_LOG,
            Material.MUSHROOM_STEM
    ));

    /*
    Used to limit the blocks that constitute a tree
     */
    public static List<Material> validTreeMaterials = new ArrayList<>(Arrays.asList(
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES,
            Material.COCOA_BEANS,
            Material.BROWN_MUSHROOM_BLOCK,
            Material.RED_MUSHROOM_BLOCK
    ));

    public static LinkedHashSet<Block> parseTree(Block block, boolean isSourceBlock) {

        /*
        Check if material is valid
         */
        if (!validMaterials.contains(block.getType())) return null;

        /*
        Check if block is surrounded by air (bottom blocks are)
        Every third block expand one block laterally until the main block line reaches air or the max height is reached
         */
        int offset = 1;
        int maxHeight = 31;

        /*
        This is used to crawl the location up the tree
         */
        Location blockLocation = block.getLocation().clone();
        Material originalMaterial = block.getType();

        for (int i = 0; i < maxHeight; i++) {

            if ((i + 1) % 2 == 0 && offset < 6)
                offset++;

            /*
            This expands the inverted search pyramid vertically
             */
            int xOffset = -offset;
            for (int j = xOffset; j < offset + 1; j++) {

                int zOffset = -offset;

                for (int k = zOffset; k < offset + 1; k++) {

                    Block thisBlock = blockLocation.clone().add(new Vector(xOffset, 0, zOffset)).getBlock();

                    if (allBlocks.contains(thisBlock)) continue;

                    /*
                    This exclusion list should include everything you may find near trees as to not invalidate trees
                    in natural forests and such
                    Construction blocks aren't included because that would bust buildings.
                     */

                    if ((thisBlock.getType().equals(Material.DIRT) ||
                            thisBlock.getType().equals(Material.COARSE_DIRT) ||
                            thisBlock.getType().equals(Material.GRASS_BLOCK)) &&
                            (i > 1) && isSourceBlock) {
                        return null;
                    }

                    if (!thisBlock.getType().equals(originalMaterial) &&
                            !validTreeMaterials.contains(thisBlock.getType()) &&
                            !thisBlock.getType().equals(Material.AIR) &&
                            !thisBlock.getType().equals(Material.VINE) &&
                            !thisBlock.getType().equals(Material.ROSE_BUSH) &&
                            !thisBlock.getType().equals(Material.ORANGE_TULIP) &&
                            !thisBlock.getType().equals(Material.PINK_TULIP) &&
                            !thisBlock.getType().equals(Material.RED_TULIP) &&
                            !thisBlock.getType().equals(Material.POPPY) &&
                            !thisBlock.getType().equals(Material.WHITE_TULIP) &&
                            !thisBlock.getType().equals(Material.OXEYE_DAISY) &&
                            !thisBlock.getType().equals(Material.AZURE_BLUET) &&
                            !thisBlock.getType().equals(Material.BLUE_ORCHID) &&
                            !thisBlock.getType().equals(Material.ALLIUM) &&
                            !thisBlock.getType().equals(Material.DANDELION) &&
                            !thisBlock.getType().equals(Material.DANDELION_YELLOW) &&
                            !thisBlock.getType().equals(Material.LILAC) &&
                            !thisBlock.getType().equals(Material.PEONY) &&
                            !thisBlock.getType().equals(Material.TALL_GRASS) &&
                            !thisBlock.getType().equals(Material.FERN) &&
                            !thisBlock.getType().equals(Material.LARGE_FERN) &&
                            !thisBlock.getType().equals(Material.DEAD_BUSH) &&
                            !thisBlock.getType().equals(Material.BROWN_MUSHROOM) &&
                            !thisBlock.getType().equals(Material.RED_MUSHROOM) &&
                            !thisBlock.getType().equals(Material.GRASS) &&
                            !thisBlock.getType().equals(Material.SPRUCE_SAPLING) &&
                            !thisBlock.getType().equals(Material.OAK_SAPLING) &&
                            !thisBlock.getType().equals(Material.JUNGLE_SAPLING) &&
                            !thisBlock.getType().equals(Material.ACACIA_SAPLING) &&
                            !thisBlock.getType().equals(Material.BIRCH_SAPLING) &&
                            !thisBlock.getType().equals(Material.DARK_OAK_SAPLING) &&
                            !thisBlock.getType().equals(Material.VOID_AIR) &&
                            !thisBlock.getType().equals(Material.CAVE_AIR) &&
                            !thisBlock.getType().equals(Material.DIRT) &&
                            !thisBlock.getType().equals(Material.COARSE_DIRT) &&
                            !thisBlock.getType().equals(Material.GRASS_BLOCK)) {

                        return null;
                    }

                    if (!thisBlock.getType().equals(Material.AIR) &&
                            !thisBlock.getType().equals(Material.CAVE_AIR) &&
                            !thisBlock.getType().equals(Material.VOID_AIR) &&
                            !thisBlock.getType().equals(Material.DIRT) &&
                            !thisBlock.getType().equals(Material.COARSE_DIRT) &&
                            !thisBlock.getType().equals(Material.GRASS_BLOCK)) {
                        allBlocks.add(thisBlock);
                    }

                    zOffset++;

                }

                xOffset++;

            }

            /*
            Increment the height of the block location
             */
            blockLocation.add(new Vector(0, 1, 0));

            /*
            Detect if it's the end of the tree
            If a block above it continues the same material type as the original block, continue with that block as the
            new source block
            If it doesn't break the loop and return the blocks list
             */
            if (blockLocation.getBlock().getType().equals(Material.AIR) || validTreeMaterials.contains(blockLocation.getBlock().getType())) {

                if (isSourceBlock && blockLocation.clone().subtract(block.getLocation().clone()).getY() < 2)
                    return null;

                ArrayList<Block> newBlocks = scanNearbyBranching(originalMaterial, blockLocation);
                if (newBlocks != null)
                    for (Block newBlock : newBlocks) {
                        LinkedHashSet<Block> newBlockList = parseTree(newBlock, false);
                        if (newBlockList == null) {
                            return null;
                        } else {
                            allBlocks.addAll(newBlocks);
                        }
                    }
                else if (blockLocation.getBlock().getType().equals(Material.AIR))
                    break;

            }

        }

        return allBlocks;

    }

    private static LinkedHashSet<Block> allBlocks = new LinkedHashSet<>();

    /*
    Some trees veer to a side as they reach the top, this scans for blocks adjacent to the air block above the trunk
     */
    private static ArrayList<Block> scanNearbyBranching(Material originalMaterial, Location location) {

        ArrayList<Block> newBlocks = new ArrayList<>();

        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                Block nearbyBlock = location.clone().add(new Vector(i, 0, j)).getBlock();
                if (!nearbyBlock.getType().equals(originalMaterial)) continue;
                if (allBlocks.contains(nearbyBlock)) continue;
                if (nearbyBlock.getLocation().equals(location)) continue;
                newBlocks.add(nearbyBlock);
            }

        if (newBlocks.isEmpty()) return null;
        return newBlocks;

    }

}
