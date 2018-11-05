package com.songoda.ultimatetimber.treefall;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class TreeLoot {

    public static void convertFallingBlock(FallingBlock fallingBlock, Player player) {

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

        if (player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE))
            if (player.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH))
                fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(fallingBlock.getBlockData().getMaterial(), 1));

        if (material.equals(Material.ACACIA_SAPLING) ||
                material.equals(Material.BIRCH_SAPLING) ||
                material.equals(Material.DARK_OAK_SAPLING) ||
                material.equals(Material.JUNGLE_SAPLING) ||
                material.equals(Material.OAK_SAPLING) ||
                material.equals(Material.SPRUCE_SAPLING))
            if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                if (player.hasPermission("ultimatetimber.bonusloot"))
                    fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));
                fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));
                return;
            }

        if (player.hasPermission("ultimatetimber.bonusloot"))
            fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));
        fallingBlock.getWorld().dropItem(fallingBlock.getLocation(), new ItemStack(material, 1));

    }

}
