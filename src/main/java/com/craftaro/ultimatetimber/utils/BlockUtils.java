package com.craftaro.ultimatetimber.utils;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatetimber.tree.ITreeBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BlockUtils {
    public static Collection<ItemStack> getBlockDrops(ITreeBlock treeBlock) {
        Set<ItemStack> drops = new HashSet<>();
        if (treeBlock.getBlock() instanceof Block) {
            Block block = (Block) treeBlock.getBlock();
            Optional<XMaterial> material = CompatibleMaterial.getMaterial(block.getType());
            if (!material.isPresent() || CompatibleMaterial.isAir(material.get())) {
                return drops;
            }
            drops.add(material.get().parseItem());
        } else if (treeBlock.getBlock() instanceof FallingBlock) {
            Optional<XMaterial> material = CompatibleMaterial.getMaterial(((FallingBlock) treeBlock.getBlock()).getBlockData().getMaterial());
            if (!material.isPresent()) {
                return drops;
            }
            drops.add(material.get().parseItem());
        }
        return drops;
    }

    public static void toggleGravityFallingBlock(FallingBlock fallingBlock, boolean applyGravity) {
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
            fallingBlock.setGravity(applyGravity);
        }
    }

    public static FallingBlock spawnFallingBlock(Location location, XMaterial material) {
        return location.getWorld().spawnFallingBlock(location, material.parseMaterial(), material.getData());
    }

    public static void configureFallingBlock(FallingBlock fallingBlock) {
        toggleGravityFallingBlock(fallingBlock, false);
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);
    }
}
