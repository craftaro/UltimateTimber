package com.songoda.ultimatetimber.utils;

import org.bukkit.Material;

public class LeafToSaplingConverter {

    /*
    Defaults to returning the same material type that is fed into it
     */
    public static Material convertLeaves(Material material) {

        switch (material) {

            case ACACIA_LEAVES:
                material = org.bukkit.Material.ACACIA_SAPLING;
                break;
            case BIRCH_LEAVES:
                material = org.bukkit.Material.BIRCH_SAPLING;
                break;
            case DARK_OAK_LEAVES:
                material = org.bukkit.Material.DARK_OAK_SAPLING;
                break;
            case JUNGLE_LEAVES:
                material = org.bukkit.Material.JUNGLE_SAPLING;
                break;
            case OAK_LEAVES:
                material = org.bukkit.Material.OAK_SAPLING;
                break;
            case SPRUCE_LEAVES:
                material = org.bukkit.Material.SPRUCE_SAPLING;
                break;
            default:
                break;

        }

        return material;

    }

}
