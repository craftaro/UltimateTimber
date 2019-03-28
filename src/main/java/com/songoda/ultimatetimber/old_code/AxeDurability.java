package com.songoda.ultimatetimber.old_code;

import com.songoda.ultimatetimber.utils.WoodToLogConverter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Random;

public class AxeDurability {
    
    private static Random random = new Random();

    /**
     * Applies damage to the axe the player is holding based on logs they broke
     * 
     * @param blocks The blocks that are part of the tree
     * @param player The player
     */
    public static void adjustAxeDamage(HashSet<Block> blocks, Player player) {
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;
        
        ItemStack item = player.getInventory().getItemInMainHand();

        Material itemType = item.getType();
        if (!(itemType.equals(Material.DIAMOND_AXE) ||
              itemType.equals(Material.GOLDEN_AXE) ||
              itemType.equals(Material.IRON_AXE) ||
              itemType.equals(Material.STONE_AXE) ||
              itemType.equals(Material.WOODEN_AXE))) return;
        
        int unbreakingLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);

        ItemMeta itemMeta = item.getItemMeta();
        Damageable damageableMeta = (Damageable) itemMeta;
        for (Block block : blocks) {
            Material material = WoodToLogConverter.convert(block.getType());
            if (isMaterialDurable(material) && checkUnbreakingChance(unbreakingLevel))
                damageableMeta.setDamage(damageableMeta.getDamage() + 1);
        }

        item.setItemMeta((ItemMeta) damageableMeta);

        if (damageableMeta.getDamage() >= item.getType().getMaxDurability())
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    }
    
    /**
     * Checks is a material should apply durability
     * 
     * @param material The material to check
     * @return If durability should be applied
     */
    private static boolean isMaterialDurable(Material material) {
        return material.equals(Material.ACACIA_LOG) ||
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
               material.equals(Material.STRIPPED_SPRUCE_LOG);
    }
    
    /**
     * Check if durbility should be applied based on the unbreaking enchantment
     * 
     * @param level The level of the unbreaking enchantment
     * @return True if durability should be applied, otherwise false
     */
    private static boolean checkUnbreakingChance(int level) {
        return ((double) 1 / (level + 1)) > random.nextDouble();
    }

}
