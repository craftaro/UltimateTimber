package com.songoda.ultimatetimber.adapter.current;

import com.songoda.ultimatetimber.adapter.IBlockData;
import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockType;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    public Collection<ItemStack> getBlockDrops(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        Set<ItemStack> drops = new HashSet<>();
        if (treeBlock.getBlock() instanceof Block) {
            Block block = (Block)treeBlock.getBlock();
            if (block.getType().equals(Material.AIR))
                return drops;
            drops.add(new ItemStack(block.getType()));
        } else if (treeBlock.getBlock() instanceof FallingBlock) {
            FallingBlock fallingBlock = (FallingBlock)treeBlock.getBlock();
            drops.add(new ItemStack(fallingBlock.getBlockData().getMaterial()));
        }
        return drops;
    }

    @Override
    public void applyToolDurability(Player player, int damage) {
        ItemStack tool = this.getItemInHand(player);
        if (!tool.hasItemMeta() || !(tool.getItemMeta() instanceof Damageable) || tool.getType().getMaxDurability() < 1)
            return;
        Bukkit.broadcastMessage("Damageable: " + tool.getType());

        int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY);
        Damageable damageable = (Damageable) tool.getItemMeta();

        int actualDamage = 0;
        for (int i = 0; i < damage; i++)
            if (Methods.checkUnbreakingChance(unbreakingLevel))
                actualDamage++;

        damageable.setDamage(damageable.getDamage() + actualDamage);
        tool.setItemMeta((ItemMeta) damageable);

        if (!this.hasEnoughDurability(tool, 1))
            this.removeItemInHand(player);
    }

    @Override
    public boolean hasEnoughDurability(ItemStack tool, int requiredAmount) {
        if (!tool.hasItemMeta() || !(tool.getItemMeta() instanceof Damageable) || tool.getType().getMaxDurability() < 1)
            return true;

        Damageable damageable = (Damageable) tool.getItemMeta();
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
    public FallingBlock spawnFallingBlock(Location location, Block block) {
        return location.getWorld().spawnFallingBlock(location, block.getBlockData());
    }

    @Override
    public void configureFallingBlock(FallingBlock fallingBlock) {
        this.toggleGravityFallingBlock(fallingBlock, false);
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);
    }

    @Override
    public void toggleGravityFallingBlock(FallingBlock fallingBlock, boolean applyGravity) {
        fallingBlock.setGravity(applyGravity);
    }

    @Override
    public void playFallingParticles(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        BlockData blockData;
        if (treeBlock.getBlock() instanceof Block) {
            blockData = ((Block)treeBlock.getBlock()).getBlockData();
        } else if (treeBlock.getBlock() instanceof FallingBlock) {
            blockData = ((FallingBlock)treeBlock.getBlock()).getBlockData();
        } else return;

        Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
        location.getWorld().spawnParticle(Particle.BLOCK_DUST, location, 10, blockData);
    }

    @Override
    public void playLandingParticles(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        BlockData blockData;
        if (treeBlock.getBlock() instanceof Block) {
            blockData = ((Block)treeBlock.getBlock()).getBlockData();
        } else if (treeBlock.getBlock() instanceof FallingBlock) {
            blockData = ((FallingBlock)treeBlock.getBlock()).getBlockData();
        } else return;

        Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
        location.getWorld().spawnParticle(Particle.BLOCK_CRACK, location, 10, blockData);
    }

    @Override
    public void playFallingSound(ITreeBlock treeBlock) {
        Location location = treeBlock.getLocation();
        location.getWorld().playSound(location, Sound.BLOCK_CHEST_OPEN, 2F, 0.1F);
    }

    @Override
    public void playLandingSound(ITreeBlock treeBlock) {
        Location location = treeBlock.getLocation();
        if (treeBlock.getTreeBlockType().equals(TreeBlockType.LOG)) {
            location.getWorld().playSound(location, Sound.BLOCK_WOOD_FALL, 2F, 0.1F);
        } else {
            location.getWorld().playSound(location, Sound.BLOCK_GRASS_BREAK, 0.5F, 0.75F);
        }
    }

}
