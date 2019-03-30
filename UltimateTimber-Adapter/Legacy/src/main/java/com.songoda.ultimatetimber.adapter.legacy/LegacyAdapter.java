package com.songoda.ultimatetimber.adapter.legacy;

import com.songoda.ultimatetimber.adapter.IBlockData;
import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import com.songoda.ultimatetimber.tree.FallingTreeBlock;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.utils.NMSUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@SuppressWarnings("deprecation")
public class LegacyAdapter implements VersionAdapter {

    @Override
    public VersionAdapterType getVersionAdapterType() {
        return VersionAdapterType.LEGACY;
    }

    @Override
    public IBlockData parseBlockDataFromString(String blockDataString) {
        return null;
    }

    @Override
    public ItemStack parseItemStackFromString(String itemStackString) {
        return null;
    }

    @Override
    public Collection<ItemStack> getBlockDrops(ITreeBlock treeBlock) {
        return null;
    }

    @Override
    public void applyToolDurability(Player player, int damage) {

    }

    @Override
    public boolean hasEnoughDurability(ItemStack tool, int requiredAmount) {
        return false;
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getItemInHand();
    }

    @Override
    public void removeItemInHand(Player player) {
        player.setItemInHand(null);
    }

    @Override
    public void playFallingParticles(ITreeBlock treeBlock) {

    }

    @Override
    public void playLandingParticles(ITreeBlock treeBlock) {

    }

    @Override
    public void playFallingSound(ITreeBlock treeBlock) {
        Location location = treeBlock.getLocation();
        if (NMSUtil.getVersionNumber() > 8) {
            location.getWorld().playSound(location, Sound.BLOCK_CHEST_OPEN, 3F, 0.1F);
        } else {
            location.getWorld().playSound(location, Sound.valueOf("CHEST_OPEN"), 3F, 0.1F);
        }
    }

    @Override
    public void playLandingSound(ITreeBlock treeBlock) {
        Location location = treeBlock.getLocation();
        if (NMSUtil.getVersionNumber() > 8) {
            location.getWorld().playSound(location, Sound.BLOCK_WOOD_FALL, 3F, 0.1F);
        } else {
            location.getWorld().playSound(location, Sound.valueOf("DIG_WOOD"), 3F, 0.1F);
        }
    }

}
