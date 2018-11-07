package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.utils.LeafToSaplingConverter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.concurrent.ThreadLocalRandom;

public class NoAnimationTreeDestroyer {

    /*
    Only ever triggers when people have tree falling animations off in the config
     */
    public static void destroyTree(LinkedHashSet<Block> blocks, boolean hasBonusLoot, boolean hasSilkTouch) {

        for (Block block : blocks) {

            Material material = LeafToSaplingConverter.convertLeaves(block.getType());

            if (material.equals(Material.AIR)) continue;
            if (material.equals(Material.VINE)) continue;

            if (hasSilkTouch) {
                if (hasBonusLoot)
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(block.getType(), 1));
                block.getWorld().dropItem(block.getLocation(), new ItemStack(block.getType(), 1));
                CustomLoot.doCustomItemDrop(block.getLocation());
                block.setType(Material.AIR);
                continue;
            }

            if (material.equals(Material.ACACIA_SAPLING) ||
                    material.equals(Material.BIRCH_SAPLING) ||
                    material.equals(Material.DARK_OAK_SAPLING) ||
                    material.equals(Material.JUNGLE_SAPLING) ||
                    material.equals(Material.OAK_SAPLING) ||
                    material.equals(Material.SPRUCE_SAPLING)) {

                if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                    if (hasBonusLoot) {
                        block.getWorld().dropItem(block.getLocation(), new ItemStack(material, 1));
                    }
                    block.getWorld().dropItem(block.getLocation(), new ItemStack(material, 1));
                    block.setType(Material.AIR);
                    CustomLoot.doCustomItemDrop(block.getLocation());
                    continue;
                } else {
                    block.setType(Material.AIR);
                    CustomLoot.doCustomItemDrop(block.getLocation());
                    continue;
                }

            }

            if (hasBonusLoot)
                block.getWorld().dropItem(block.getLocation(), new ItemStack(material, 1));
            block.getWorld().dropItem(block.getLocation(), new ItemStack(material, 1));
            block.setType(Material.AIR);
            CustomLoot.doCustomItemDrop(block.getLocation());

        }

    }

}
