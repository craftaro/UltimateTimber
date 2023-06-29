package com.songoda.ultimatetimber.manager;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockType;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SaplingManager extends Manager {

    private Random random;
    private Set<Location> protectedSaplings;

    public SaplingManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        this.random = new Random();
        this.protectedSaplings = new HashSet<>();
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    /**
     * Replants a sapling given a TreeDefinition and Location
     * Takes into account config settings
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param treeBlock The ITreeBlock to replant for
     */
    public void replantSapling(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        if (!ConfigurationManager.Setting.REPLANT_SAPLINGS.getBoolean())
            return;

        Block block = treeBlock.getLocation().getBlock();
        if (!block.getType().equals(Material.AIR) || treeBlock.getTreeBlockType().equals(TreeBlockType.LEAF))
            return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.internalReplant(treeDefinition, treeBlock), 1L);
    }

    /**
     * Randomly replants a sapling given a TreeDefinition and Location
     * Takes into account config settings
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param treeBlock The ITreeBlock to replant for
     */
    public void replantSaplingWithChance(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        if (!ConfigurationManager.Setting.FALLING_BLOCKS_REPLANT_SAPLINGS.getBoolean() || !treeBlock.getLocation().getBlock().getType().equals(Material.AIR))
            return;

        double chance = ConfigurationManager.Setting.FALLING_BLOCKS_REPLANT_SAPLINGS_CHANCE.getDouble();
        if (this.random.nextDouble() > chance / 100)
            return;

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
        for (CompatibleMaterial soilMaterial : treeDefinitionManager.getPlantableSoilMaterial(treeDefinition)) {
            if (soilMaterial.equals(CompatibleMaterial.getMaterial(blockBelow))) {
                isValidSoil = true;
                break;
            }
        }

        if (!isValidSoil)
            return;

        CompatibleMaterial material = treeDefinition.getSaplingMaterial();
        material.applyToBlock(block);

        int cooldown = ConfigurationManager.Setting.REPLANT_SAPLINGS_COOLDOWN.getInt();
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
