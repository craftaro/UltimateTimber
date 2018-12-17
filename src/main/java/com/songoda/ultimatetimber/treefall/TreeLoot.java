package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.utils.LeafToSaplingConverter;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.concurrent.ThreadLocalRandom;

public class TreeLoot {

    public static void convertFallingBlock(FallingBlock fallingBlock, boolean hasBonusLoot, boolean hasSilkTouch) {

        Material material = LeafToSaplingConverter.convertLeaves(fallingBlock.getBlockData().getMaterial());

        if (material.equals(Material.VINE))
            return;

        if (hasSilkTouch) {
            if (hasBonusLoot)
                fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(fallingBlock.getBlockData().getMaterial(), 1));
            fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(fallingBlock.getBlockData().getMaterial(), 1));
            CustomLoot.doCustomItemDrop(fallingBlock.getLocation());
            return;
        }

        if (material.equals(Material.BROWN_MUSHROOM_BLOCK)) {
            fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(Material.BROWN_MUSHROOM, 1));
            return;
        }

        if (material.equals(Material.RED_MUSHROOM_BLOCK)) {
            fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(Material.RED_MUSHROOM, 1));
            return;
        }

        if (material.equals(Material.MUSHROOM_STEM)) {
            return;
        }

        if (material.equals(Material.ACACIA_SAPLING) ||
                material.equals(Material.BIRCH_SAPLING) ||
                material.equals(Material.DARK_OAK_SAPLING) ||
                material.equals(Material.JUNGLE_SAPLING) ||
                material.equals(Material.OAK_SAPLING) ||
                material.equals(Material.SPRUCE_SAPLING)) {

            if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                if (hasBonusLoot) {
                    fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));
                }
                fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));
                CustomLoot.doCustomItemDrop(fallingBlock.getLocation());
                return;
            } else {
                CustomLoot.doCustomItemDrop(fallingBlock.getLocation());
                return;
            }

        }

        if (hasBonusLoot)
            fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));
        fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));

    }


}
