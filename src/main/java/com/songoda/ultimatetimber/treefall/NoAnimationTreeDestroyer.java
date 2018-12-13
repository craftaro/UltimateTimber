package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.utils.LeafToSaplingConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class NoAnimationTreeDestroyer {

    /*
    Only ever triggers when people have tree falling animations off in the config
     */
    public static void destroyTree(HashSet<Block> blocks, boolean hasBonusLoot, boolean hasSilkTouch, Block minedLog) {

        Block mainLog = getMainLog(minedLog.getLocation());

        Material oldMaterial = mainLog.getType();
        Byte oldData = mainLog.getData();
        Location mainLogLocation = mainLog.getLocation().clone();

        for (Block block : blocks) {

            Material material = LeafToSaplingConverter.convertLeaves(block.getType());

            if (material.equals(Material.AIR)) continue;
            if (material.equals(Material.VINE)) continue;

            ItemStack toDrop = getItem(material);

            if (hasSilkTouch) {
                if (hasBonusLoot)
                    block.getWorld().dropItem(block.getLocation(), toDrop.clone());
                block.getWorld().dropItem(block.getLocation(), toDrop.clone());
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

            if (hasBonusLoot)
                block.getWorld().dropItem(block.getLocation(), toDrop.clone());
            block.getWorld().dropItem(block.getLocation(), toDrop.clone());


            block.setType(Material.AIR);
            CustomLoot.doCustomItemDrop(block.getLocation());

        }

        if(mainLogLocation.getBlock().getType() == Material.AIR) {
            TreeReplant.replaceOriginalBlock(mainLogLocation.getBlock());
        }

    }

    static Block getMainLog(Location loc){

        int maxHeight = 7;

        Location clonedLocation = loc.getBlock().getLocation();
        Block toReturn = null;

        Location check1 = clonedLocation.clone();

        if(check1.add(0,-1,0).getBlock().getType() != loc.getBlock().getType()){
            return clonedLocation.getBlock();
        }

        for(int i = 0; i < maxHeight;i++){

            if(clonedLocation.add(0,-1,0).getBlock().getType() == loc.getBlock().getType()){

                Location secondClone = clonedLocation.clone();

                if(secondClone.add(0,-1,0).getBlock().getType() != loc.getBlock().getType()){
                    toReturn = clonedLocation.getBlock();
                }

            }

        }
        return toReturn;

    }

    static ItemStack getItem(Material material){

        if(material == Material.BROWN_MUSHROOM_BLOCK){
            return new ItemStack(Material.BROWN_MUSHROOM, 1);
        } else if(material == Material.RED_MUSHROOM_BLOCK){
            return new ItemStack(Material.RED_MUSHROOM, 1);
        }
        return new ItemStack(material, 1);

    }

}
