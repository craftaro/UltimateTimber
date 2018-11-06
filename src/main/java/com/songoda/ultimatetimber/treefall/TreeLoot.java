package com.songoda.ultimatetimber.treefall;

import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class TreeLoot {

    public static void convertFallingBlock(FallingBlock fallingBlock, boolean hasBonusLoot, boolean hasSilkTouch) {

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
                material = fallingBlock.getBlockData().getMaterial();

        }

        if (material.equals(Material.VINE))
            return;

        if (hasSilkTouch) {
            if (hasBonusLoot)
                fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));
            fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(fallingBlock.getBlockData().getMaterial(), 1));
            return;
        }

        if (material.equals(Material.ACACIA_SAPLING) ||
                material.equals(Material.BIRCH_SAPLING) ||
                material.equals(Material.DARK_OAK_SAPLING) ||
                material.equals(Material.JUNGLE_SAPLING) ||
                material.equals(Material.OAK_SAPLING) ||
                material.equals(Material.SPRUCE_SAPLING)) {
            if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                if (hasBonusLoot)
                    fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));
                fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));
                return;
            } else return;
        }


        if (hasBonusLoot)
            fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));
        fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));

    }

}
