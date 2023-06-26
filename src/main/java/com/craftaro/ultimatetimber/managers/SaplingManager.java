package com.craftaro.ultimatetimber.managers;

import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.ultimatetimber.tree.ITreeBlock;
import com.craftaro.ultimatetimber.tree.TreeBlockType;
import com.craftaro.ultimatetimber.tree.TreeDefinition;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SaplingManager {

    private final UltimateTimber plugin;
    private final Set<Location> protectedSaplings = new HashSet<>();
    public SaplingManager(UltimateTimber plugin) {
        this.plugin = plugin;
    }

    /**
     * Replants a sapling given a TreeDefinition and Location
     * Takes into account config settings
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param treeBlock The ITreeBlock to replant for
     */
    public void replantSapling(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        if (!plugin.getMainConfig().getBoolean("Settings.Replant Saplings")) {
            return;
        }

        Block block = treeBlock.getLocation().getBlock();
        if (!block.getType().equals(Material.AIR) || treeBlock.getTreeBlockType().equals(TreeBlockType.LEAF)) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> internalReplant(treeDefinition, treeBlock), 1L);
    }

    /**
     * Randomly replants a sapling given a TreeDefinition and Location
     * Takes into account config settings
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param treeBlock The ITreeBlock to replant for
     */
    public void replantSaplingWithChance(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        if (!plugin.getMainConfig().getBoolean("Settings.Falling Blocks Replant Saplings") || !treeBlock.getLocation().getBlock().getType().equals(Material.AIR)) {
            return;
        }

        double chance = plugin.getMainConfig().getDouble("Settings.Falling Blocks Replant Saplings Chance");
        if (ThreadLocalRandom.current().nextDouble() > chance / 100) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.internalReplant(treeDefinition, treeBlock), 1L);
    }

    /**
     * Replants a sapling given a TreeDefinition and Location
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param treeBlock The ITreeBlock to replant for
     */
    private void internalReplant(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        TreeDefinitionManager treeDefinitionManager = this.plugin.getTreeDefinitionManager();

        Block block = treeBlock.getLocation().getBlock();
        Block blockBelow = block.getRelative(BlockFace.DOWN);
        boolean isValidSoil = false;
        for (XMaterial soilMaterial : treeDefinitionManager.getPlantableSoilMaterial(treeDefinition)) {
            if (soilMaterial.equals(XMaterial.matchXMaterial(blockBelow.getType()))) {
                isValidSoil = true;
                break;
            }
        }

        if (!isValidSoil)
            return;

        XMaterial material = treeDefinition.getSaplingMaterial();
        block.setType(material.parseMaterial());

        int cooldown = plugin.getMainConfig().getInt("Settings.Replant Saplings Cooldown");
        if (cooldown != 0) {
            this.protectedSaplings.add(block.getLocation());
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.protectedSaplings.remove(block.getLocation()), cooldown * 20L);
        }
    }

    /**
     * Gets if a sapling is protected
     *
     * @param block The Block to check
     * @return True if the sapling is protected, otherwise false
     */
    public boolean isSaplingProtected(Block block) {
        return this.protectedSaplings.contains(block.getLocation());
    }
}
