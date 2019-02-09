package com.songoda.ultimatetimber.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class WoodToLogConverter {
    
    private static Map<Material, Material> woodToLog;
    
    static {
        woodToLog = new HashMap<>();
        woodToLog.put(Material.ACACIA_WOOD, Material.ACACIA_LOG);
        woodToLog.put(Material.STRIPPED_ACACIA_WOOD, Material.STRIPPED_ACACIA_LOG);
        woodToLog.put(Material.BIRCH_WOOD, Material.BIRCH_LOG);
        woodToLog.put(Material.STRIPPED_BIRCH_WOOD, Material.STRIPPED_BIRCH_LOG);
        woodToLog.put(Material.DARK_OAK_WOOD, Material.DARK_OAK_LOG);
        woodToLog.put(Material.STRIPPED_DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_LOG);
        woodToLog.put(Material.JUNGLE_WOOD, Material.JUNGLE_LOG);
        woodToLog.put(Material.STRIPPED_JUNGLE_WOOD, Material.STRIPPED_JUNGLE_LOG);
        woodToLog.put(Material.OAK_WOOD, Material.OAK_LOG);
        woodToLog.put(Material.STRIPPED_OAK_WOOD, Material.STRIPPED_OAK_LOG);
        woodToLog.put(Material.SPRUCE_WOOD, Material.SPRUCE_LOG);
        woodToLog.put(Material.STRIPPED_SPRUCE_WOOD, Material.STRIPPED_SPRUCE_LOG);
    }

    /**
     * Converts a Wood Material to its corresponding Log Material
     * 
     * @param material The wood material
     * @return The corresonding log material
     */
    public static Material convert(Material material) {
        Material converted = woodToLog.get(material);
        if (converted == null)
            return material;
        return converted;
    }

}
