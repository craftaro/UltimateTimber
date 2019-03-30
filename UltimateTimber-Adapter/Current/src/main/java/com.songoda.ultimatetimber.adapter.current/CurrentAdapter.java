package com.songoda.ultimatetimber.adapter.current;

import com.songoda.ultimatetimber.adapter.IBlockData;
import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import com.songoda.ultimatetimber.tree.FallingTreeBlock;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashSet;

public class CurrentAdapter implements VersionAdapter {

    @Override
    public VersionAdapterType getVersionAdapterType() {
        return VersionAdapterType.CURRENT;
    }

    @Override
    public IBlockData parseBlockDataFromString(String blockDataString) {
        return new CurrentBlockData(Material.matchMaterial(blockDataString));
    }

    @Override
    public ItemStack parseItemStackFromString(String itemStackString) {
        return new ItemStack(Material.matchMaterial(itemStackString));
    }

    @Override
    public Collection<ItemStack> getBlockDrops(ITreeBlock treeBlock) {
        if (treeBlock.getBlock() instanceof Block) {
            Block block = (Block)treeBlock.getBlock();
            return block.getDrops(); // TODO: Do this properly
        }
        return new HashSet<>();
    }

    @Override
    public void applyToolDurability(Player player, int damage) {
        ItemStack tool = this.getItemInHand(player);

        if (!tool.hasItemMeta() || !(tool.getItemMeta() instanceof Damageable))
            return;

        int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY);
        Damageable damageable = (Damageable)tool.getItemMeta();

        int actualDamage = 0;
        for (int i = 0; i < damage; i++)
            if (Methods.checkUnbreakingChance(unbreakingLevel))
                actualDamage++;

        damageable.setDamage(damageable.getDamage() + actualDamage);
        tool.setItemMeta((ItemMeta) damageable);


    }

    @Override
    public boolean hasEnoughDurability(ItemStack tool, int requiredAmount) {
        if (!tool.hasItemMeta() || !(tool.getItemMeta() instanceof Damageable))
            return false;

        Damageable damageable = (Damageable)tool.getItemMeta();
        int durabilityRemaining = tool.getType().getMaxDurability() - damageable.getDamage();
        return durabilityRemaining > requiredAmount;
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    @Override
    public void removeItemInHand(Player player) {
        player.getInventory().setItemInMainHand(null);
    }

    @Override
    public void playFallingParticles(TreeBlockSet<Block> treeBlocks) {

    }

    @Override
    public void playLandingParticles(FallingTreeBlock treeBlock) {

    }

    @Override
    public void playFallingSound(TreeBlockSet<Block> treeBlocks) {
        Location location = treeBlocks.getInitialLogBlock().getLocation();
        location.getWorld().playSound(location, Sound.BLOCK_CHEST_OPEN, 3F, 0.1F);
    }

    @Override
    public void playLandingSound(FallingTreeBlock treeBlock) {
        Location location = treeBlock.getLocation();
        location.getWorld().playSound(location, Sound.BLOCK_WOOD_FALL, 3F, 0.1F);
    }

}
