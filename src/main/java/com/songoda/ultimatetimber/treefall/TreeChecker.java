package com.songoda.ultimatetimber.treefall;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
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
            Material.STRIPPED_SPRUCE_LOG
    ));

    /*
    Used to limit the blocks that constitute a tree
     */
    private static List<Material> validSurroundingMaterials = new ArrayList<>(Arrays.asList(
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES,
            Material.COCOA_BEANS
    ));

    public static ArrayList<Block> parseTree(Block block) {
        /*
        Check if material is valid
         */
        if (!validMaterials.contains(block.getType())) return null;

        ArrayList<Block> blocks = new ArrayList<>();

        boolean containsSecondaryBlock = false;

        /*
        Check if block is surrounded by air (bottom blocks are)
        Every third block expand one block laterally until the main block line reaches air or the max height is reached
         */
        int offset = 1;
        int maxHeight = 31;

        for (int i = 0; i < maxHeight; i++) {

            if ((i + 1) % 2 == 0 && offset < 6)
                offset++;

            /*
            This expands the inverted search pyramid vertically
             */
            int xOffset = -offset;
            for (int j = xOffset - 1; j < offset; j++) {

                int zOffset = -offset;

                for (int k = zOffset - 1; k < offset; k++) {

                    Block thisBlock = block.getLocation().clone().add(new Vector(xOffset, i, zOffset)).getBlock();

                    /*
                    This exclusion list should include everything you may find near trees as to not invalidate trees
                    in natural forests and such
                    Construction blocks aren't included because that would bust buildings.
                     */
                    if (!thisBlock.getType().equals(block.getType()) &&
                            !validSurroundingMaterials.contains(thisBlock.getType()) &&
                            !thisBlock.getType().equals(Material.AIR) &&
                            !thisBlock.getType().equals(Material.VINE) &&
                            !thisBlock.getType().equals(Material.ROSE_BUSH) &&
                            !thisBlock.getType().equals(Material.ORANGE_TULIP) &&
                            !thisBlock.getType().equals(Material.PINK_TULIP) &&
                            !thisBlock.getType().equals(Material.RED_TULIP) &&
                            !thisBlock.getType().equals(Material.POPPY) &&
                            !thisBlock.getType().equals(Material.WHITE_TULIP) &&
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
                            !thisBlock.getType().equals(Material.CAVE_AIR))
                        return null;

                    if (validSurroundingMaterials.contains(thisBlock.getType()))
                        containsSecondaryBlock = true;

                    if (!thisBlock.getType().equals(Material.AIR))
                        blocks.add(thisBlock);

                    zOffset++;

                }

                xOffset++;

            }

            if (block.getLocation().clone().add(new Vector(0, i, 0)).getBlock().getType().equals(Material.AIR))
                if (i > 1)
                    break;
                else
                    return null;

        }

        /*
        If there are no leaves, don't see it as a tree
         */
        if (!containsSecondaryBlock) return null;

        return blocks;

    }

}
