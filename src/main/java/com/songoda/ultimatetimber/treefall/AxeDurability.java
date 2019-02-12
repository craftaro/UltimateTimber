package com.songoda.ultimatetimber.treefall;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.songoda.ultimatetimber.utils.WoodToLogConverter;

import java.util.HashSet;

class AxeDurability {

    /*
    This class handles all durability damage dealt to the axe used to chop down the tree, only takes into account
    wood blocks chopped down
     */
    static void adjustAxeDamage(HashSet<Block> blocks, Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!(item.getType().equals(Material.DIAMOND_AXE) ||
                item.getType().equals(Material.GOLDEN_AXE) ||
                item.getType().equals(Material.IRON_AXE) ||
                item.getType().equals(Material.STONE_AXE) ||
                item.getType().equals(Material.WOODEN_AXE))) return;

        ItemMeta itemMeta = item.getItemMeta();
        Damageable damageableMeta = (Damageable) itemMeta;
        for (Block block : blocks) {
            Material material = WoodToLogConverter.convert(block.getType());
            if (material.equals(Material.ACACIA_LOG) ||
                    material.equals(Material.BIRCH_LOG) ||
                    material.equals(Material.DARK_OAK_LOG) ||
                    material.equals(Material.JUNGLE_LOG) ||
                    material.equals(Material.OAK_LOG) ||
                    material.equals(Material.SPRUCE_LOG) ||
                    material.equals(Material.STRIPPED_ACACIA_LOG) ||
                    material.equals(Material.STRIPPED_BIRCH_LOG) ||
                    material.equals(Material.STRIPPED_DARK_OAK_LOG) ||
                    material.equals(Material.STRIPPED_JUNGLE_LOG) ||
                    material.equals(Material.STRIPPED_OAK_LOG) ||
                    material.equals(Material.STRIPPED_SPRUCE_LOG))
                damageableMeta.setDamage(damageableMeta.getDamage() + 1);
        }
            

        item.setItemMeta((ItemMeta) damageableMeta);

        if (item.getDurability() >= item.getType().getMaxDurability())
            player.setItemInHand(new ItemStack(Material.AIR));
    }

}
