package com.songoda.ultimatetimber.old_code;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.utils.WoodToLogConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class TreeReplant {

    private static List<Location> timeout = new ArrayList<>();

    static void replaceOriginalBlock(Block block) {
        boolean isTimeout = UltimateTimber.getInstance().getConfig().getBoolean(DefaultConfig.TIMEOUT_BREAK);
        Material material = WoodToLogConverter.convert(block.getType());

        if (!UltimateTimber.getInstance().getConfig().getBoolean(DefaultConfig.REPLANT_SAPLING)) {
            block.setType(Material.AIR);
            return;
        }

        Block below = block.getRelative(BlockFace.DOWN);
        Material belowType = below.getType();
        if (belowType.equals(Material.AIR) && UltimateTimber.getInstance().getConfig().getBoolean(DefaultConfig.ENTIRE_TREE_BASE)) {
            if (isValidGround(below.getRelative(BlockFace.DOWN).getType())) {
                if (isTimeout) {
                    timeout.add(below.getLocation());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateTimber.getInstance(), () -> timeout.remove(below.getLocation()), 20 * 5);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateTimber.getInstance(), () -> performReplacement(below, material), 1);
            }
        }
        
        if (!isValidGround(belowType)) {
            block.setType(Material.AIR);
            return;
        }

        if (isTimeout) {
            timeout.add(block.getLocation());
            Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateTimber.getInstance(), () -> timeout.remove(block.getLocation()), 20 * 5);
        }
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateTimber.getInstance(), () -> performReplacement(block, material), 1);
    }
    
    static void performReplacement(Block block, Material material) {
        switch (material) {
            case ACACIA_LOG:
            case STRIPPED_ACACIA_LOG:
                block.setType(Material.ACACIA_SAPLING);
                return;
            case BIRCH_LOG:
            case STRIPPED_BIRCH_LOG:
                block.setType(Material.BIRCH_SAPLING);
                return;
            case DARK_OAK_LOG:
            case STRIPPED_DARK_OAK_LOG:
                block.setType(Material.DARK_OAK_SAPLING);
                return;
            case JUNGLE_LOG:
            case STRIPPED_JUNGLE_LOG:
                block.setType(Material.JUNGLE_SAPLING);
                return;
            case OAK_LOG:
            case STRIPPED_OAK_LOG:
                block.setType(Material.OAK_SAPLING);
                return;
            case SPRUCE_LOG:
            case STRIPPED_SPRUCE_LOG:
                block.setType(Material.SPRUCE_SAPLING);
                return;
            case BROWN_MUSHROOM_BLOCK:
                block.setType(Material.BROWN_MUSHROOM);
                return;
            case RED_MUSHROOM_BLOCK:
                block.setType(Material.RED_MUSHROOM);
                return;
            default:
                block.setType(Material.AIR);
        }
    }

    static void leafFallReplant(FallingBlock fallingBlock) {

        Material material;

        switch (fallingBlock.getBlockData().getMaterial()) {
            case ACACIA_LEAVES:
                material = Material.ACACIA_SAPLING;
                break;
            case BIRCH_LEAVES:
                material = Material.BIRCH_SAPLING;
                break;
            case DARK_OAK_LEAVES:
                material = Material.DARK_OAK_SAPLING;
                break;
            case JUNGLE_LEAVES:
                material = Material.JUNGLE_SAPLING;
                break;
            case OAK_LEAVES:
                material = Material.OAK_SAPLING;
                break;
            case SPRUCE_LEAVES:
                material = Material.SPRUCE_SAPLING;
                break;
            default:
                material = null;
        }

        if (material == null) return;

        if (ThreadLocalRandom.current().nextDouble() > 0.01) return;

        Block block = fallingBlock.getLocation().clone().subtract(new Vector(0, 1, 0)).getBlock();

        if (isValidGround(block.getType())) {
            Block blockAbove = block.getLocation().clone().add(new Vector(0, 1, 0)).getBlock();
            if (blockAbove.getType().equals(Material.AIR))
                fallingBlock.getLocation().getBlock().setType(material);
        }

    }

    static boolean isTimeout(Block block) {
        return timeout.contains(block.getLocation());
    }
    
    private static boolean isValidGround(Material material) {
        return material.equals(Material.DIRT) || material.equals(Material.COARSE_DIRT) || material.equals(Material.PODZOL) || material.equals(Material.GRASS_BLOCK);
    }

}
