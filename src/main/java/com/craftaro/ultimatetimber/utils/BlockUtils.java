package com.craftaro.ultimatetimber.utils;

import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatetimber.tree.ITreeBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BlockUtils {

    public static Collection<ItemStack> getBlockDrops(ITreeBlock treeBlock) {
        Set<ItemStack> drops = new HashSet<>();
        XMaterial material = null;
        if (treeBlock.getBlock() instanceof Block) {
            Block block = (Block)treeBlock.getBlock();
            material = XMaterial.matchXMaterial(block.getType());
        } else if (treeBlock.getBlock() instanceof FallingBlock) {
            material = XMaterial.matchXMaterial(((FallingBlock)treeBlock.getBlock()).getMaterial());
        }

        if (material == null || material == XMaterial.AIR) {
            return drops;
        }

        drops.add(material.parseItem());

        return drops;
    }

    public static void toggleGravityFallingBlock(FallingBlock fallingBlock, boolean applyGravity) {
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9))
            fallingBlock.setGravity(applyGravity);
    }

    public static FallingBlock spawnFallingBlock(Location location, XMaterial material) {
        return location.getWorld().spawnFallingBlock(location, material.parseMaterial().createBlockData());
    }

    public static void configureFallingBlock(FallingBlock fallingBlock) {
        toggleGravityFallingBlock(fallingBlock, false);
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);
    }

}
