package com.songoda.ultimatetimber.treefall;

import java.util.HashSet;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

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

        // Drop loot and plant a new sapling
        for (Block block : blocks) {
            TreeLoot.dropTreeLoot(block.getBlockData(), block.getLocation().clone().add(0.5, 0.5, 0.5), hasBonusLoot, hasSilkTouch);

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
