package com.craftaro.ultimatetimber.manager;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XBlock;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.ultimatetimber.tree.ITreeBlock;
import com.craftaro.ultimatetimber.tree.TreeBlockType;
import com.craftaro.ultimatetimber.tree.TreeDefinition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SaplingManager extends Manager {
    private final Random random;
    private final Set<Location> protectedSaplings;

    public SaplingManager(UltimateTimber plugin) {
        super(plugin);
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
     * @param treeBlock      The ITreeBlock to replant for
     */
    public void replantSapling(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        if (!ConfigurationManager.Setting.REPLANT_SAPLINGS.getBoolean()) {
            return;
        }

        Block block = treeBlock.getLocation().getBlock();
        if (block.getType() != Material.AIR || treeBlock.getTreeBlockType() == TreeBlockType.LEAF) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.internalReplant(treeDefinition, treeBlock), 1L);
    }

    /**
     * Randomly replants a sapling given a TreeDefinition and Location
     * Takes into account config settings
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param treeBlock      The ITreeBlock to replant for
     */
    public void replantSaplingWithChance(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        if (!ConfigurationManager.Setting.FALLING_BLOCKS_REPLANT_SAPLINGS.getBoolean() || !CompatibleMaterial.isAir(CompatibleMaterial.getMaterial(treeBlock.getLocation().getBlock().getType()).get())) {
            return;
        }

        double chance = ConfigurationManager.Setting.FALLING_BLOCKS_REPLANT_SAPLINGS_CHANCE.getDouble();
        if (this.random.nextDouble() > chance / 100) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.internalReplant(treeDefinition, treeBlock), 1L);
    }

    /**
     * Replants a sapling given a TreeDefinition and Location
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param treeBlock      The ITreeBlock to replant for
     */
    private void internalReplant(TreeDefinition treeDefinition, ITreeBlock treeBlock) {
        TreeDefinitionManager treeDefinitionManager = this.plugin.getTreeDefinitionManager();

        Block block = treeBlock.getLocation().getBlock();
        Block blockBelow = block.getRelative(BlockFace.DOWN);
        boolean isValidSoil = false;
        for (XMaterial soilMaterial : treeDefinitionManager.getPlantableSoilMaterial(treeDefinition)) {
            if (soilMaterial == CompatibleMaterial.getMaterial(blockBelow.getType()).orElse(null)) {
                isValidSoil = true;
                break;
            }
        }

        if (!isValidSoil) {
            return;
        }

        XMaterial material = treeDefinition.getSaplingMaterial();
        XBlock.setType(block, material);

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
