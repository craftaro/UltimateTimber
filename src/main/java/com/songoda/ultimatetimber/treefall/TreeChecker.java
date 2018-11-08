package com.songoda.ultimatetimber.treefall;

import org.bukkit.Bukkit;
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

    /*
    A list of materials found in a forest, allows the plugin to work in dense woods
     */
    private static List<Material> forestMaterials = new ArrayList<>(Arrays.asList(
            Material.AIR,
            Material.CAVE_AIR,
            Material.VOID_AIR,
            Material.VINE,
            Material.ROSE_BUSH,
            Material.ORANGE_TULIP,
            Material.PINK_TULIP,
            Material.RED_TULIP,
            Material.POPPY,
            Material.WHITE_TULIP,
            Material.OXEYE_DAISY,
            Material.AZURE_BLUET,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.DANDELION,
            Material.DANDELION_YELLOW,
            Material.LILAC,
            Material.PEONY,
            Material.TALL_GRASS,
            Material.FERN,
            Material.LARGE_FERN,
            Material.DEAD_BUSH,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.GRASS,
            Material.SPRUCE_SAPLING,
            Material.OAK_SAPLING,
            Material.JUNGLE_SAPLING,
            Material.ACACIA_SAPLING,
            Material.BIRCH_SAPLING,
            Material.DARK_OAK_SAPLING,
            Material.DIRT,
            Material.COARSE_DIRT,
            Material.GRASS_BLOCK
    ));

    public LinkedHashSet<Block> validTreeHandler(Block block, boolean isSourceBlock) {

        LinkedHashSet<Block> blocks = parseTree(block, isSourceBlock);

        if (blocks == null)
            return null;

        boolean containsLeaves = false;

        for (Block localBlock : blocks)
            if (TreeChecker.validTreeMaterials.contains(localBlock.getType())) {
                containsLeaves = true;
                break;
            }

        if (!containsLeaves)
            return null;

        return blocks;

    }

    public LinkedHashSet<Block> parseTree(Block block, boolean isSourceBlock) {

        /*
        Check if material is parsed by this plugin
         */
        if (!validMaterials.contains(block.getType())) return null;

        /*
        offset determines the search radius aroudn the main trunk
        maxheight sets the maximum height the plugin will crawl through to find a tree
         */
        int offset = 0;
        int maxHeight = 31;

        /*
        centralBlockLocation is used to keep track of the main trunk
        originalMaterial keeps track of what log type the plugin is looking for
         */
        Location centralBlockLocation = block.getLocation().clone();
        Material originalMaterial = block.getType();

        for (int i = 0; i < maxHeight; i++) {

            /*
            Offset increases as it goes up the tree in order to cover the area a tree would take
             */
            if (offset < 6)
                offset++;

            /*
            The search works in a reverse conical shape
             */
            for (int x = -offset; x < offset + 1; x++) {

                for (int z = -offset; z < offset + 1; z++) {

                    Block thisBlock = centralBlockLocation.clone().add(new Vector(x, 0, z)).getBlock();

                    if (allBlocks.contains(thisBlock))
                        continue;

                    /*
                    This adds a bit of tolerance for trees that exist on dirt ledges
                     */
                    if ((thisBlock.getType().equals(Material.DIRT) ||
                            thisBlock.getType().equals(Material.COARSE_DIRT) ||
                            thisBlock.getType().equals(Material.GRASS_BLOCK)) &&
                            (i > 1) && isSourceBlock) {
                        return null;
                    }
                    /*
                    Exclude anything that isn't a part of a tree or a forest to avoid destroying houses
                     */
                    if (!thisBlock.getType().equals(originalMaterial) &&
                            !validTreeMaterials.contains(thisBlock.getType()) &&
                            !forestMaterials.contains(thisBlock.getType()))
                        return null;


                    /*
                    This adds blocks to later be felled
                     */
                    if (validMaterials.contains(thisBlock.getType()) || validTreeMaterials.contains(thisBlock.getType())) {
                        allBlocks.add(thisBlock);
                    }

                }

            }

            /*
            Continue crawling up the main trunk
             */
            centralBlockLocation.add(new Vector(0, 1, 0));

            /*
            Detect if it's the end of the tree
            If a block above it continues the same material type as the original block, continue with that block as the
            new source block
            If it doesn't and it's air or a leaf, scan for adjacent blocks to see if the tree continues in another
            direction. This is necessary for acacias and some of the wider tree variants.
             */
            if (centralBlockLocation.getBlock().getType().equals(Material.AIR) || validTreeMaterials.contains(centralBlockLocation.getBlock().getType())) {

                if (isSourceBlock && centralBlockLocation.clone().subtract(block.getLocation().clone()).getY() < 2)
                    return null;

                ArrayList<Block> newBlocks = scanNearbyBranching(originalMaterial, centralBlockLocation);
                if (newBlocks != null)
                    for (Block newBlock : newBlocks) {
                        LinkedHashSet<Block> newBlockList = parseTree(newBlock, false);
                        if (newBlockList == null)
                            return null;
                        else
                            allBlocks.addAll(newBlocks);
                    }
                else if (centralBlockLocation.getBlock().getType().equals(Material.AIR))
                    break;

            }

        }

        return allBlocks;

    }

    /*
    This stores all the blocks returned later on
     */
    private LinkedHashSet<Block> allBlocks = new LinkedHashSet<>();

    /*
    This method scans for branching atop the tree when the crawled upon block of the tree trunk is either air or a leaf block
     */
    private ArrayList<Block> scanNearbyBranching(Material originalMaterial, Location location) {

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
