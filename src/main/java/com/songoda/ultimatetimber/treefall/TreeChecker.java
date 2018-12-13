package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.utils.LogToLeafConverter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.*;

public class TreeChecker {

    /*
    This stores all the blocks returned later on
     */
    private HashSet<Block> allBlocks = new HashSet<>();

    public HashSet<Block> validTreeHandler(Block block) {

        HashSet<Block> blocks = parseTree(block);

        if (blocks == null)
            return null;

        boolean containsLeaves = false;

        for (Block localBlock : blocks) {
            if (TreeChecker.validTreeMaterials.contains(localBlock.getType())) {
                containsLeaves = true;
                break;
            } else if(TreeChecker.validMaterials.contains(localBlock.getType())){
                containsLeaves = true;
                break;
            }
        }
        if (!containsLeaves)
            return null;

        return blocks;

    }

    /**
     * This parses a tree; returns a hashset if it is a valid tree, or returns null if it isn't
     *
     * @param block block the player originally destroys
     * @return returns null if the tree isn't valid or all blocks in the tree if it isn't
     */
    public HashSet<Block> parseTree(Block block) {

        /*
        Check if material is parsed by this plugin
         */
        if (!validMaterials.contains(block.getType())) return null;

        /*
        offset determines the search radius around the main trunk
        maxheight sets the maximum height the plugin will crawl through to find a tree
         */
        int offset = 5;
        int maxHeight = 31;

        /*
        Keep track of the location of the original block to see how much we've deviated from it
         */
        Location centralBlockLocation = block.getLocation().clone();
        /*
        Keep a list of all location that are considered to be a part of the trunk. This is necessary as scans are made
        around each one as the search crawls up to detect leaves or building blocks.
         */
        HashSet<Location> trunkList = new HashSet<>();
        trunkList.add(centralBlockLocation);
        Material originalMaterial = block.getType();

        for (int i = 0; i < maxHeight; i++) {

            /*
            For some reason, using the iterator to gradually clear hashset elements isn't working as the hashset
            claims not to contain said elements. This is a bit of a dirty workarounf dor that issue.
             */
            HashSet<Location> cleanLogSet = new HashSet<>();
            for (Location location : trunkList)
                if (location.getBlock().getType().equals(originalMaterial) ||
                        location.getBlock().getType().equals(LogToLeafConverter.convert(originalMaterial)) ||
                        location.clone().add(new Vector(0, -1, 0)).getBlock().getType().equals(originalMaterial))
                    cleanLogSet.add(location);

            if (cleanLogSet.isEmpty()) {
                if (i > 2)
                    return allBlocks;
                else
                    return null;
            }


            trunkList = cleanLogSet;

            /*
            Search for adjacent trunks
             */
            Iterator<Location> iterator = trunkList.iterator();
            HashSet<Location> expandedTrunkSet = new HashSet<>();
            while (iterator.hasNext()) {

                Location trunkLocation = iterator.next();
                allBlocks.add(trunkLocation.getBlock());

                int radMin, radMax;

                if (i > 5) {
                    radMin = -2;
                    radMax = 3;
                } else {
                    radMin = -1;
                    radMax = 2;
                }

                for (int x = radMin; x < radMax; x++)
                    for (int z = radMin; z < radMax; z++) {

                        Location currentLocation = trunkLocation.clone().add(new Vector(x, 0, z));
                        if (Math.abs(currentLocation.getX() - trunkLocation.getX()) > offset ||
                                Math.abs(currentLocation.getZ() - trunkLocation.getZ()) > offset)
                            continue;
                        if (currentLocation.getBlock().getType().equals(originalMaterial)) {
                            expandedTrunkSet.add(currentLocation);
                            allBlocks.add(currentLocation.getBlock());
                        }

                    }

            }

            trunkList.addAll(expandedTrunkSet);

            /*
             Check if the tree is valid and add leaves
             */
            for (Location location : trunkList) {

                int radMin, radMax;

                if (i > 5) {
                    radMin = -3;
                    radMax = 4;
                } else {
                    radMin = -2;
                    radMax = 3;
                }


                for (int x = radMin; x < radMax; x++)
                    for (int z = radMin; z < radMax; z++) {

                        Block currentBlock = location.clone().add(x, 0, z).getBlock();

                        /*
                        Check if this block is already in the block list
                         */
                        if (allBlocks.contains(currentBlock))
                            continue;

                        /*
                        Add a bit of tolerance for trees that exist on dirt ledges
                         */
                        if ((currentBlock.getType().equals(Material.DIRT) ||
                                currentBlock.getType().equals(Material.COARSE_DIRT) ||
                                currentBlock.getType().equals(Material.GRASS_BLOCK)) &&
                                i > 1) {
                            return null;
                        }

                        /*
                        Exclude anything that isn't a part of a tree or a forest to avoid destroying houses
                        */
                        if (!validMaterials.contains(currentBlock.getType()) &&
                                !validTreeMaterials.contains(currentBlock.getType()) &&
                                !forestMaterials.contains(currentBlock.getType()))
                            return null;

                        /*
                        This adds blocks to later be felled
                        Only take blocks of the same tree type
                        */
                        if ((LogToLeafConverter.convert(originalMaterial) != null &&
                                LogToLeafConverter.convert(originalMaterial).equals(currentBlock.getType())) ||
                                (originalMaterial.equals(Material.MUSHROOM_STEM) &&
                                        (currentBlock.getType().equals(Material.RED_MUSHROOM_BLOCK) ||
                                                currentBlock.getType().equals(Material.BROWN_MUSHROOM_BLOCK)))) {
                            allBlocks.add(currentBlock);

                        }

                    }

                location.add(new Vector(0, 1, 0));

            }

        }

        return allBlocks;

    }

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
            Material.GRASS_BLOCK,
            Material.SNOW,
            Material.SNOW_BLOCK
    ));

}
