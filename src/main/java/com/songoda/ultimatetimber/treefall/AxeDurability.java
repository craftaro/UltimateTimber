package com.songoda.ultimatetimber.treefall;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class AxeDurability {

    public static void adjustAxeDamage(ArrayList<Block> blocks, Player player) {

        if (!(player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) ||
                player.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE))) return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        Damageable damageableMeta = (Damageable) itemMeta;
        damageableMeta.setDamage(damageableMeta.getDamage() + blocks.size());
        itemStack.setItemMeta((ItemMeta) damageableMeta);

    }

}
