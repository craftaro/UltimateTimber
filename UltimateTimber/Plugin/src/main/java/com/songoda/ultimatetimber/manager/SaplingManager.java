package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.adapter.IBlockData;
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
     * @param location The Location to plant the sapling
     */
    public void replantSapling(TreeDefinition treeDefinition, Location location) {
        if (!ConfigurationManager.Setting.REPLANT_SAPLINGS.getBoolean())
            return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.ultimateTimber, () -> this.internalReplant(treeDefinition, location), 1L);
    }

    /**
     * Randomly replants a sapling given a TreeDefinition and Location
     * Takes into account config settings
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param location The Location to plant the sapling
     */
    public void replantSaplingWithChance(TreeDefinition treeDefinition, Location location) {
        if (!ConfigurationManager.Setting.FALLING_BLOCKS_REPLANT_SAPLINGS.getBoolean())
            return;

        double chance = ConfigurationManager.Setting.FALLING_BLOCKS_REPLANT_SAPLINGS_CHANCE.getDouble();
        if (this.random.nextDouble() > chance / 100)
            return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.ultimateTimber, () -> this.internalReplant(treeDefinition, location), 1L);
    }

    /**
     * Replants a sapling given a TreeDefinition and Location
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param location The Location to plant the sapling
     */
    private void internalReplant(TreeDefinition treeDefinition, Location location) {
        Block block = location.getBlock();
        if (!block.getType().equals(Material.AIR))
            return;

        Block blockBelow = block.getRelative(BlockFace.DOWN);
        boolean isValidSoil = false;
        for (IBlockData soilBlockData : treeDefinition.getPlantableSoilBlockData()) {
            if (soilBlockData.isSimilar(blockBelow)) {
                isValidSoil = true;
                break;
            }
        }

        if (!isValidSoil)
            return;

        IBlockData saplingBlockData = treeDefinition.getSaplingBlockData();
        saplingBlockData.setBlock(location.getBlock());

        int cooldown = ConfigurationManager.Setting.REPLANT_SAPLINGS_COOLDOWN.getInt();
        if (cooldown != 0) {
            this.protectedSaplings.add(location);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.ultimateTimber, () -> this.protectedSaplings.remove(location), cooldown * 20L);
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
