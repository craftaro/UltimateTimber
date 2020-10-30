package com.songoda.ultimatetimber.utils;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ParticleUtils {

    public static void playFallingParticles(ITreeBlock treeBlock) {
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
            BlockData blockData;
            if (treeBlock.getBlock() instanceof Block) {
                blockData = ((Block) treeBlock.getBlock()).getBlockData();
            } else if (treeBlock.getBlock() instanceof FallingBlock) {
                blockData = ((FallingBlock) treeBlock.getBlock()).getBlockData();
            } else return;

            Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
            location.getWorld().spawnParticle(Particle.BLOCK_DUST, location, 10, blockData);
        } else {

            Collection<ItemStack> blockDrops = BlockUtils.getBlockDrops(treeBlock);
            if (!blockDrops.iterator().hasNext())
                return;

            Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
            if (ServerVersion.isServerVersion(ServerVersion.V1_8)) {
                location.getWorld().playEffect(location, Effect.SMOKE, 4);
            } else {
                location.getWorld().spawnParticle(Particle.BLOCK_DUST, location, 10, blockDrops.iterator().next().getData());
            }
        }
    }

    public static void playLandingParticles(ITreeBlock treeBlock) {
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
            BlockData blockData;
            if (treeBlock.getBlock() instanceof Block) {
                blockData = ((Block) treeBlock.getBlock()).getBlockData();
            } else if (treeBlock.getBlock() instanceof FallingBlock) {
                blockData = ((FallingBlock) treeBlock.getBlock()).getBlockData();
            } else return;

            Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
            location.getWorld().spawnParticle(Particle.BLOCK_CRACK, location, 10, blockData);
        } else {
            Collection<ItemStack> blockDrops = BlockUtils.getBlockDrops(treeBlock);
            if (!blockDrops.iterator().hasNext())
                return;

            Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
                location.getWorld().spawnParticle(Particle.BLOCK_CRACK, location, 10, blockDrops.iterator().next().getData());
            } else {
                location.getWorld().playEffect(location, Effect.SMOKE, 4);
            }
        }
    }
}
