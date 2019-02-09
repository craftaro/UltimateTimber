package com.songoda.ultimatetimber.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class LogToLeafConverter {
    
    private static Map<Material, Material> logToLeaf;
    
    static {
        logToLeaf = new HashMap<>();
        logToLeaf.put(Material.ACACIA_LOG, Material.ACACIA_LEAVES);
        logToLeaf.put(Material.ACACIA_WOOD, Material.ACACIA_LEAVES);
        logToLeaf.put(Material.STRIPPED_ACACIA_LOG, Material.ACACIA_LEAVES);
        logToLeaf.put(Material.STRIPPED_ACACIA_WOOD, Material.ACACIA_LEAVES);
        logToLeaf.put(Material.BIRCH_LOG, Material.BIRCH_LEAVES);
        logToLeaf.put(Material.BIRCH_WOOD, Material.BIRCH_LEAVES);
        logToLeaf.put(Material.STRIPPED_BIRCH_LOG, Material.BIRCH_LEAVES);
        logToLeaf.put(Material.STRIPPED_BIRCH_WOOD, Material.BIRCH_LEAVES);
        logToLeaf.put(Material.DARK_OAK_LOG, Material.DARK_OAK_LEAVES);
        logToLeaf.put(Material.DARK_OAK_WOOD, Material.DARK_OAK_LEAVES);
        logToLeaf.put(Material.STRIPPED_DARK_OAK_LOG, Material.DARK_OAK_LEAVES);
        logToLeaf.put(Material.STRIPPED_DARK_OAK_WOOD, Material.DARK_OAK_LEAVES);
        logToLeaf.put(Material.JUNGLE_LOG, Material.JUNGLE_LEAVES);
        logToLeaf.put(Material.JUNGLE_WOOD, Material.JUNGLE_LEAVES);
        logToLeaf.put(Material.STRIPPED_JUNGLE_LOG, Material.JUNGLE_LEAVES);
        logToLeaf.put(Material.STRIPPED_JUNGLE_WOOD, Material.JUNGLE_LEAVES);
        logToLeaf.put(Material.OAK_LOG, Material.OAK_LEAVES);
        logToLeaf.put(Material.OAK_WOOD, Material.OAK_LEAVES);
        logToLeaf.put(Material.STRIPPED_OAK_LOG, Material.OAK_LEAVES);
        logToLeaf.put(Material.STRIPPED_OAK_WOOD, Material.OAK_LEAVES);
        logToLeaf.put(Material.SPRUCE_LOG, Material.SPRUCE_LEAVES);
        logToLeaf.put(Material.SPRUCE_WOOD, Material.SPRUCE_LEAVES);
        logToLeaf.put(Material.STRIPPED_SPRUCE_LOG, Material.SPRUCE_LEAVES);
        logToLeaf.put(Material.STRIPPED_SPRUCE_WOOD, Material.SPRUCE_LEAVES);
        logToLeaf.put(Material.MUSHROOM_STEM, Material.MUSHROOM_STEM);
    }

    public static Material convert(Material material) {
        return logToLeaf.get(material);
    }

}
