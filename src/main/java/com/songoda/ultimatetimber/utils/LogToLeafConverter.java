package com.songoda.ultimatetimber.utils;

import org.bukkit.Material;

public class LogToLeafConverter {

    public static Material convert(Material material) {

        switch (material) {

            case ACACIA_LOG:
            case STRIPPED_ACACIA_LOG:
                return Material.ACACIA_LEAVES;
            case BIRCH_LOG:
            case STRIPPED_BIRCH_LOG:
                return Material.BIRCH_LEAVES;
            case DARK_OAK_LOG:
            case STRIPPED_DARK_OAK_LOG:
                return Material.DARK_OAK_LEAVES;
            case JUNGLE_LOG:
            case STRIPPED_JUNGLE_LOG:
                return Material.JUNGLE_LEAVES;
            case OAK_LOG:
            case STRIPPED_OAK_LOG:
                return Material.OAK_LEAVES;
            case SPRUCE_LOG:
            case STRIPPED_SPRUCE_LOG:
                return Material.SPRUCE_LEAVES;
            default:
                return null;

        }

    }

}
