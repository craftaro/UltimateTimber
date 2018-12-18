package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.utils.LeafToSaplingConverter;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

        if (itemStack.getType().equals(Material.LOG)){
            if (fallingBlock.getBlockData() == 0 || fallingBlock.getBlockData() == 4 || fallingBlock.getBlockData() == 8)
                itemStack = new ItemStack(Material.LOG, 1, (byte) 0);
            if (fallingBlock.getBlockData() == 1 || fallingBlock.getBlockData() == 5 || fallingBlock.getBlockData() == 9)
                itemStack = new ItemStack(Material.LOG, 1, (byte) 1);
            if (fallingBlock.getBlockData() == 2 || fallingBlock.getBlockData() == 6 || fallingBlock.getBlockData() == 10)
                itemStack = new ItemStack(Material.LOG, 1, (byte) 2);
            if (fallingBlock.getBlockData() == 4 || fallingBlock.getBlockData() == 7 || fallingBlock.getBlockData() == 11)
                itemStack = new ItemStack(Material.LOG, 1, (byte) 3);
        }

        if (itemStack.getType().equals(Material.LOG_2)){
            if (fallingBlock.getBlockData() == 0 || fallingBlock.getBlockData() == 4 || fallingBlock.getBlockData() == 8)
                itemStack = new ItemStack(Material.LOG_2, 1, (byte) 0);
            if (fallingBlock.getBlockData() == 1 || fallingBlock.getBlockData() == 5 || fallingBlock.getBlockData() == 9)
                itemStack = new ItemStack(Material.LOG_2, 1, (byte) 1);
        }

        if (hasBonusLoot)
            fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), itemStack);
        fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), itemStack);

    }

    public static ItemStack convertBlock(Block block, boolean hasSilkTouch) {


        ItemStack itemStack = new ItemStack(block.getType(), 1);

        if (itemStack.getType().equals(Material.VINE)) return itemStack;

        if (itemStack.getType().equals(Material.LEAVES) || itemStack.getType().equals(Material.LEAVES_2))
            itemStack = LeafToSaplingConverter.convertLeaves(block, hasSilkTouch);

        if (itemStack.getType().equals(Material.HUGE_MUSHROOM_2)) {
            if(!hasSilkTouch)  itemStack = new ItemStack(Material.RED_MUSHROOM, 1);
        }

        if (itemStack.getType().equals(Material.HUGE_MUSHROOM_1)) {
            if(!hasSilkTouch) itemStack = new ItemStack(Material.BROWN_MUSHROOM, 1);
        }

    }


}
