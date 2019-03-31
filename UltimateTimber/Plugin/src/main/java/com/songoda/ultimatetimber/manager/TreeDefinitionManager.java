package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.adapter.IBlockData;
import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockType;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import com.songoda.ultimatetimber.tree.TreeLoot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TreeDefinitionManager extends Manager {

    private final Random random;
    private Set<TreeDefinition> treeDefinitions;
    private Set<IBlockData> globalPlantableSoil;
    private Set<TreeLoot> globalLogLoot, globalLeafLoot;
    private Set<ItemStack> globalRequiredTools;

    public TreeDefinitionManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        this.random = new Random();
        this.treeDefinitions = new HashSet<>();
        this.globalPlantableSoil = new HashSet<>();
        this.globalLogLoot = new HashSet<>();
        this.globalLeafLoot = new HashSet<>();
        this.globalRequiredTools = new HashSet<>();
    }

    @Override
    public void reload() {
        this.treeDefinitions.clear();
        this.globalPlantableSoil.clear();
        this.globalLogLoot.clear();
        this.globalLeafLoot.clear();
        this.globalRequiredTools.clear();

        VersionAdapter versionAdapter = this.ultimateTimber.getVersionAdapter();
        ConfigurationManager configurationManager = this.ultimateTimber.getConfigurationManager();
        YamlConfiguration config = configurationManager.getConfig();

        // Load tree settings
        ConfigurationSection treeSection = config.getConfigurationSection("trees");
        for (String key : treeSection.getKeys(false)) {
            ConfigurationSection tree = treeSection.getConfigurationSection(key);

            Set<IBlockData> logBlockData = new HashSet<>();
            Set<IBlockData> leafBlockData = new HashSet<>();
            IBlockData saplingBlockData;
            Set<IBlockData> plantableSoilBlockData = new HashSet<>();
            int maxLeafDistanceFromLog;
            boolean detectLeavesDiagonally;
            boolean dropOriginalLog;
            boolean dropOriginalLeaf;
            Set<TreeLoot> logLoot = new HashSet<>();
            Set<TreeLoot> leafLoot = new HashSet<>();
            Set<ItemStack> requiredTools = new HashSet<>();

            for (String blockDataString : tree.getStringList("logs"))
                logBlockData.add(versionAdapter.parseBlockDataFromString(blockDataString));

            for (String blockDataString : tree.getStringList("leaves"))
                leafBlockData.add(versionAdapter.parseBlockDataFromString(blockDataString));

            saplingBlockData = versionAdapter.parseBlockDataFromString(tree.getString("sapling"));

            for (String blockDataString : tree.getStringList("plantable-soil"))
                plantableSoilBlockData.add(versionAdapter.parseBlockDataFromString(blockDataString));

            maxLeafDistanceFromLog = tree.getInt("max-leaf-distance-from-log");
            detectLeavesDiagonally = tree.getBoolean("search-for-leaves-diagonally");
            dropOriginalLog = tree.getBoolean("drop-original-log");
            dropOriginalLeaf = tree.getBoolean("drop-original-leaf");

            ConfigurationSection logLootSection = tree.getConfigurationSection("log-loot");
            if (logLootSection != null)
                for (String lootKey : logLootSection.getKeys(false))
                    logLoot.add(this.getTreeLootEntry(versionAdapter, TreeBlockType.LOG, logLootSection.getConfigurationSection(lootKey)));

            ConfigurationSection leafLootSection = tree.getConfigurationSection("leaf-loot");
            if (leafLootSection != null)
                for (String lootKey : leafLootSection.getKeys(false))
                    leafLoot.add(this.getTreeLootEntry(versionAdapter, TreeBlockType.LEAF, leafLootSection.getConfigurationSection(lootKey)));

            for (String itemStackString : tree.getStringList("required-tools"))
                requiredTools.add(versionAdapter.parseItemStackFromString(itemStackString));

            this.treeDefinitions.add(new TreeDefinition(key, logBlockData, leafBlockData, saplingBlockData, plantableSoilBlockData, maxLeafDistanceFromLog, detectLeavesDiagonally, dropOriginalLog, dropOriginalLeaf, logLoot, leafLoot, requiredTools));
        }

        // Load global plantable soil
        for (String blockDataString : config.getStringList("global-plantable-soil"))
            this.globalPlantableSoil.add(versionAdapter.parseBlockDataFromString(blockDataString));

        // Load global log drops
        ConfigurationSection logSection = config.getConfigurationSection("global-log-loot");
        if (logSection != null)
            for (String lootKey : logSection.getKeys(false))
                this.globalLogLoot.add(this.getTreeLootEntry(versionAdapter, TreeBlockType.LOG, logSection.getConfigurationSection(lootKey)));

        // Load global leaf drops
        ConfigurationSection leafSection = config.getConfigurationSection("global-leaf-loot");
        if (leafSection != null)
            for (String lootKey : leafSection.getKeys(false))
                this.globalLeafLoot.add(this.getTreeLootEntry(versionAdapter, TreeBlockType.LEAF, leafSection.getConfigurationSection(lootKey)));

        // Load global tools
        for (String itemStackString : config.getStringList("global-required-tools"))
            this.globalRequiredTools.add(versionAdapter.parseItemStackFromString(itemStackString));
    }

    @Override
    public void disable() {
        this.treeDefinitions.clear();
    }

    /**
     * Gets a Set of possible TreeDefinitions that match the given Block
     *
     * @param block The Block to check
     * @return A Set of TreeDefinitions for the given Block
     */
    public Set<TreeDefinition> getTreeDefinitionsForLog(Block block) {
        return this.narrowTreeDefinition(this.treeDefinitions, block, TreeBlockType.LOG);
    }

    /**
     * Narrows a Set of TreeDefinitions down to ones matching the given Block and TreeBlockType
     *
     * @param possibleTreeDefinitions The possible TreeDefinitions
     * @param block The Block to narrow to
     * @param treeBlockType The TreeBlockType of the given Block
     * @return A Set of TreeDefinitions narrowed down
     */
    public Set<TreeDefinition> narrowTreeDefinition(Set<TreeDefinition> possibleTreeDefinitions, Block block, TreeBlockType treeBlockType) {
        Set<TreeDefinition> matchingTreeDefinitions = new HashSet<>();
        switch (treeBlockType) {
            case LOG:
                for (TreeDefinition treeDefinition : possibleTreeDefinitions) {
                    for (IBlockData logBlockData : treeDefinition.getLogBlockData()) {
                        if (logBlockData.isSimilar(block)) {
                            matchingTreeDefinitions.add(treeDefinition);
                            break;
                        }
                    }
                }
                break;
            case LEAF:
                for (TreeDefinition treeDefinition : possibleTreeDefinitions) {
                    for (IBlockData leafBlockData : treeDefinition.getLeafBlockData()) {
                        if (leafBlockData.isSimilar(block)) {
                            matchingTreeDefinitions.add(treeDefinition);
                            break;
                        }
                    }
                }
                break;
        }

        return matchingTreeDefinitions;
    }

    /**
     * Checks if a given tool is valid for any tree definitions, also takes into account global tools
     *
     * @param tool The tool to check
     * @return True if the tool is allowed for toppling any trees
     */
    public boolean isToolValidForAnyTreeDefinition(ItemStack tool) {
        if (ConfigurationManager.Setting.IGNORE_REQUIRED_TOOLS.getBoolean())
            return true;
        for (TreeDefinition treeDefinition : this.treeDefinitions)
            for (ItemStack requiredTool : treeDefinition.getRequiredTools())
                if (requiredTool.getType().equals(tool.getType()))
                    return true;
        for (ItemStack requiredTool : this.globalRequiredTools)
            if (requiredTool.getType().equals(tool.getType()))
                return true;
        return false;
    }

    /**
     * Checks if a given tool is valid for a given tree definition, also takes into account global tools
     *
     * @param treeDefinition The TreeDefinition to use
     * @param tool The tool to check
     * @return True if the tool is allowed for toppling the given TreeDefinition
     */
    public boolean isToolValidForTreeDefinition(TreeDefinition treeDefinition, ItemStack tool) {
        if (ConfigurationManager.Setting.IGNORE_REQUIRED_TOOLS.getBoolean())
            return true;
        for (ItemStack requiredTool : treeDefinition.getRequiredTools())
            if (requiredTool.getType().equals(tool.getType()))
                return true;
        for (ItemStack requiredTool : this.globalRequiredTools)
            if (requiredTool.getType().equals(tool.getType()))
                return true;
        return false;
    }

    /**
     * Tries to spawn loot for a given TreeBlock with the given TreeDefinition for a given player
     *
     * @param treeDefinition The TreeDefinition to use
     * @param treeBlock The TreeBlock to drop for
     * @param player The Player to drop for
     */
    public void dropTreeLoot(TreeDefinition treeDefinition, ITreeBlock treeBlock, Player player) {
        VersionAdapter versionAdapter = this.ultimateTimber.getVersionAdapter();

        boolean addToInventory = ConfigurationManager.Setting.ADD_ITEMS_TO_INVENTORY.getBoolean();
        ItemStack itemInHand = this.ultimateTimber.getVersionAdapter().getItemInHand(player);
        boolean hasSilkTouch = itemInHand != null && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
        boolean hasBonusChance = player.hasPermission("ultimatetimber.bonusloot");
        Set<ItemStack> lootedItems = new HashSet<>();
        Set<String> lootedCommands = new HashSet<>();

        // Get the loot that we should try to drop
        Set<TreeLoot> toTry = new HashSet<>();
        switch (treeBlock.getTreeBlockType()) {
            case LOG:
                toTry.addAll(treeDefinition.getLogLoot());
                toTry.addAll(this.globalLogLoot);
                if (treeDefinition.shouldDropOriginalLog() || hasSilkTouch)
                    lootedItems.addAll(versionAdapter.getBlockDrops(treeDefinition, treeBlock));
                break;
            case LEAF:
                toTry.addAll(treeDefinition.getLeafLoot());
                toTry.addAll(this.globalLeafLoot);
                if (treeDefinition.shouldDropOriginalLeaf() || hasSilkTouch)
                    lootedItems.addAll(versionAdapter.getBlockDrops(treeDefinition, treeBlock));
                break;
        }

        // Roll the dice
        double bonusLootMultiplier = ConfigurationManager.Setting.BONUS_LOOT_MULTIPLIER.getDouble();
        for (TreeLoot treeLoot : toTry) {
            double chance = hasBonusChance ? treeLoot.getChance() * bonusLootMultiplier : treeLoot.getChance();
            if (this.random.nextDouble() > chance / 100)
                continue;
            if (treeLoot.hasItem())
                lootedItems.add(treeLoot.getItem());
            if (treeLoot.hasCommand())
                lootedCommands.add(treeLoot.getCommand());
        }

        // Add to inventory or drop on ground
        if (addToInventory) {
            Set<ItemStack> extraItems = new HashSet<>();
            for (ItemStack lootedItem : lootedItems)
                extraItems.addAll(player.getInventory().addItem(lootedItem).values());
            Location location = player.getLocation();
            for (ItemStack extraItem : extraItems)
                location.getWorld().dropItemNaturally(location, extraItem);
        } else {
            Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
            for (ItemStack lootedItem : lootedItems)
                location.getWorld().dropItemNaturally(location, lootedItem);
        }

        // Run looted commands
        for (String lootedCommand : lootedCommands)
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    lootedCommand.replace("%player", player.getName())
                                 .replace("%type", treeDefinition.getKey())
                                 .replace("%xPos", treeBlock.getLocation().getBlockX() + "")
                                 .replace("%yPos", treeBlock.getLocation().getBlockY() + "")
                                 .replace("%zPos", treeBlock.getLocation().getBlockZ() + ""));
    }

    /**
     * Gets all possible plantable soil blocks for the given tree definition
     *
     * @param treeDefinition The TreeDefinition
     * @return A Set of IBlockData of plantable soil
     */
    public Set<IBlockData> getPlantableSoilBlockData(TreeDefinition treeDefinition) {
        Set<IBlockData> plantableSoilBlockData = new HashSet<>();
        plantableSoilBlockData.addAll(treeDefinition.getPlantableSoilBlockData());
        plantableSoilBlockData.addAll(this.globalPlantableSoil);
        return plantableSoilBlockData;
    }

    /**
     * Gets a TreeLoot entry from a ConfigurationSection
     *
     * @param versionAdapter The VersionAdapter to use
     * @param treeBlockType The TreeBlockType to use
     * @param configurationSection The ConfigurationSection
     * @return A TreeLoot entry from the section
     */
    private TreeLoot getTreeLootEntry(VersionAdapter versionAdapter, TreeBlockType treeBlockType, ConfigurationSection configurationSection) {
        String material = configurationSection.getString("material");
        ItemStack item = material != null ? versionAdapter.parseItemStackFromString(material) : null;
        String command = configurationSection.getString("command");
        double chance = configurationSection.getDouble("chance");
        return new TreeLoot(treeBlockType, item, command, chance);
    }

}
