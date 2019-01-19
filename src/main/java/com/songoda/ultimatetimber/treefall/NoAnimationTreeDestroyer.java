package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.utils.LeafToSaplingConverter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class NoAnimationTreeDestroyer {

    /*
    Only ever triggers when people have tree falling animations off in the config
     */
    static void destroyTree(HashSet<Block> blocks, boolean hasBonusLoot, boolean hasSilkTouch) {

        Material leavesType = null;

        if (!blocks.stream().filter(b -> b.getType() == Material.BROWN_MUSHROOM_BLOCK).collect(Collectors.toList()).isEmpty()) {

            leavesType = Material.BROWN_MUSHROOM_BLOCK;

        } else if (!blocks.stream().filter(b -> b.getType() == Material.RED_MUSHROOM_BLOCK).collect(Collectors.toList()).isEmpty()) {
            leavesType = Material.RED_MUSHROOM_BLOCK;
        }

        for (Block block : blocks) {

            Material material = LeafToSaplingConverter.convertLeaves(block.getType());

            if (material.equals(Material.AIR) || material.equals(Material.VINE)) continue;

            ItemStack toDrop = getItem(material);

            if (material.equals(Material.ACACIA_SAPLING) ||
                    material.equals(Material.BIRCH_SAPLING) ||
                    material.equals(Material.DARK_OAK_SAPLING) ||
                    material.equals(Material.JUNGLE_SAPLING) ||
                    material.equals(Material.OAK_SAPLING) ||
                    material.equals(Material.SPRUCE_SAPLING)) {

                if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                    if (hasBonusLoot) {
                        block.getWorld().dropItem(block.getLocation(), toDrop.clone());
                    }
                    block.getWorld().dropItem(block.getLocation(), toDrop.clone());
                    block.setType(Material.AIR);
                    CustomLoot.doCustomItemDrop(block.getLocation());
                    continue;
                } else {
                    block.setType(Material.AIR);
                    CustomLoot.doCustomItemDrop(block.getLocation());
                    continue;
                }

            }

            if (hasSilkTouch) {
                if (hasBonusLoot)
                    block.getWorld().dropItem(block.getLocation(), toDrop.clone());
                block.getWorld().dropItem(block.getLocation(), toDrop.clone());
                CustomLoot.doCustomItemDrop(block.getLocation());
                block.setType(Material.AIR);
                continue;
            }

            if (hasBonusLoot)
                block.getWorld().dropItem(block.getLocation(), toDrop.clone());
            block.getWorld().dropItem(block.getLocation(), toDrop.clone());


            block.setType(Material.AIR);
            CustomLoot.doCustomItemDrop(block.getLocation());

            if (leavesType != null) {
                TreeReplant.replaceOriginalBlock(block, leavesType);
            } else {
                TreeReplant.replaceOriginalBlock(block);
            }

        }
        
    }

    static ItemStack getItem(Material material) {

        if (material == Material.BROWN_MUSHROOM_BLOCK) {
            return new ItemStack(Material.BROWN_MUSHROOM, 1);
        } else if (material == Material.RED_MUSHROOM_BLOCK) {
            return new ItemStack(Material.RED_MUSHROOM, 1);
        }
        return new ItemStack(material, 1);

    }

}
