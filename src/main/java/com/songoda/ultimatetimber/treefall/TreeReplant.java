package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class TreeReplant {

    public static void replaceOriginalBlock(Block block) {

        if (!DefaultConfig.configuration.getBoolean(DefaultConfig.REPLANT_SAPLING)) {
            block.setType(Material.AIR);
            return;
        }

        if (!block.getLocation().clone().subtract(new Vector(0, 1, 0)).getBlock().getType().equals(Material.DIRT) &&
                !block.getLocation().clone().subtract(new Vector(0, 1, 0)).getBlock().getType().equals(Material.COARSE_DIRT)) {
            block.setType(Material.AIR);
            return;
        }

        Material material = block.getType();

        new BukkitRunnable() {
            @Override
            public void run() {
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
                    default:
                        block.setType(Material.AIR);
                }
            }
        }.runTaskLater(UltimateTimber.plugin, 1);

    }

    public static void leafFallReplant(FallingBlock fallingBlock) {

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

        if (ThreadLocalRandom.current().nextDouble() > 0.05) return;

        Block block = fallingBlock.getLocation().clone().subtract(new Vector(0, 1, 0)).getBlock();

        if (block.getType().equals(Material.DIRT) || block.getType().equals(Material.COARSE_DIRT) || block.getType().equals(Material.GRASS_BLOCK)) {
            Block blockAbove = block.getLocation().clone().add(new Vector(0, 1, 0)).getBlock();
            if (blockAbove.getType().equals(Material.AIR))
                fallingBlock.getLocation().getBlock().setType(material);

        }

    }

}
