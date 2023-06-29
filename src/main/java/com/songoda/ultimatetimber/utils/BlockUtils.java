package com.songoda.ultimatetimber.utils;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.ultimatetimber.tree.ITreeBlock;
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
        if (treeBlock.getBlock() instanceof Block) {
            Block block = (Block)treeBlock.getBlock();
            CompatibleMaterial material = CompatibleMaterial.getMaterial(block);
            if (material.isAir())
                return drops;
            drops.add(CompatibleMaterial.getMaterial(block).getItem());
        } else if (treeBlock.getBlock() instanceof FallingBlock) {
            CompatibleMaterial material = CompatibleMaterial.getMaterial((FallingBlock)treeBlock.getBlock());
            if (material == null)
                return drops;
            drops.add(material.getItem());
        }
        return drops;
    }

    public static void toggleGravityFallingBlock(FallingBlock fallingBlock, boolean applyGravity) {
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9))
            fallingBlock.setGravity(applyGravity);
    }

    public static FallingBlock spawnFallingBlock(Location location, CompatibleMaterial material) {
        return location.getWorld().spawnFallingBlock(location, material.getMaterial(), material.getData());
    }

    public static void configureFallingBlock(FallingBlock fallingBlock) {
        toggleGravityFallingBlock(fallingBlock, false);
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);
    }

}
