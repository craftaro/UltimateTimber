package com.songoda.ultimatetimber.treefall;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashSet;

public class AxeDurability {

    /*
    This class handles all durability damage dealt to the axe used to chop down the tree, only takes into account
    wood blocks chopped down
     */
    public static void adjustAxeDamage(LinkedHashSet<Block> blocks, Player player) {

        if (!(player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE))) return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        Damageable damageableMeta = (Damageable) itemMeta;
        for (Block block : blocks)
            if (block.getType().equals(Material.ACACIA_LOG) ||
                    block.getType().equals(Material.BIRCH_LOG) ||
                    block.getType().equals(Material.DARK_OAK_LOG) ||
                    block.getType().equals(Material.JUNGLE_LOG) ||
                    block.getType().equals(Material.OAK_LOG) ||
                    block.getType().equals(Material.SPRUCE_LOG) ||
                    block.getType().equals(Material.STRIPPED_ACACIA_LOG) ||
                    block.getType().equals(Material.STRIPPED_BIRCH_LOG) ||
                    block.getType().equals(Material.STRIPPED_DARK_OAK_LOG) ||
                    block.getType().equals(Material.STRIPPED_JUNGLE_LOG) ||
                    block.getType().equals(Material.STRIPPED_OAK_LOG) ||
                    block.getType().equals(Material.STRIPPED_SPRUCE_LOG))
                damageableMeta.setDamage(damageableMeta.getDamage() + 1);

        itemStack.setItemMeta((ItemMeta) damageableMeta);

    }

}
