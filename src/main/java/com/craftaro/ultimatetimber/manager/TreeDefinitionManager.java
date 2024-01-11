package com.craftaro.ultimatetimber.manager;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.hooks.McMMOHook;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.ultimatetimber.UltimateTimber;
import com.google.common.base.Strings;
import com.craftaro.ultimatetimber.tree.ITreeBlock;
import com.craftaro.ultimatetimber.tree.TreeBlockType;
import com.craftaro.ultimatetimber.tree.TreeDefinition;
import com.craftaro.ultimatetimber.tree.TreeLoot;
import com.craftaro.ultimatetimber.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class TreeDefinitionManager extends Manager {
    private final Random random;
    private final Set<TreeDefinition> treeDefinitions;
    private final Set<XMaterial> globalPlantableSoil;
    private final Set<TreeLoot> globalLogLoot, globalLeafLoot, globalEntireTreeLoot;
    private final Set<ItemStack> globalRequiredTools;

    private boolean globalAxeRequired;
    private ItemStack requiredAxe;
    private String requiredAxeKey;

    public TreeDefinitionManager(UltimateTimber plugin) {
        super(plugin);
        this.random = new Random();
        this.treeDefinitions = new HashSet<>();
        this.globalPlantableSoil = new HashSet<>();
        this.globalLogLoot = new HashSet<>();
        this.globalLeafLoot = new HashSet<>();
        this.globalEntireTreeLoot = new HashSet<>();
        this.globalRequiredTools = new HashSet<>();
    }

    @Override
    public void reload() {
        this.treeDefinitions.clear();
        this.globalPlantableSoil.clear();
        this.globalLogLoot.clear();
        this.globalLeafLoot.clear();
        this.globalEntireTreeLoot.clear();
        this.globalRequiredTools.clear();

        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration config = configurationManager.getConfig();

        // Load tree settings
        ConfigurationSection treeSection = config.getConfigurationSection("trees");
        top:
        for (String key : treeSection.getKeys(false)) {
            ConfigurationSection tree = treeSection.getConfigurationSection(key);

            Set<XMaterial> logMaterials = new HashSet<>();
            Set<XMaterial> leafMaterials = new HashSet<>();
            XMaterial saplingMaterial;
            Set<XMaterial> plantableSoilMaterial = new HashSet<>();
            double maxLogDistanceFromTrunk;
            int maxLeafDistanceFromLog;
            boolean detectLeavesDiagonally;
            boolean dropOriginalLog;
            boolean dropOriginalLeaf;
            Set<TreeLoot> logLoot = new HashSet<>();
            Set<TreeLoot> leafLoot = new HashSet<>();
            Set<TreeLoot> entireTreeLoot = new HashSet<>();
            Set<ItemStack> requiredTools = new HashSet<>();
            boolean requiredAxe;

            for (String materialString : tree.getStringList("logs")) {
                Optional<XMaterial> material = CompatibleMaterial.getMaterial(materialString);
                if (!material.isPresent() || !material.get().isSupported()) {
                    continue top;
                }
                logMaterials.add(material.get());
            }

            for (String materialString : tree.getStringList("leaves")) {
                Optional<XMaterial> material = CompatibleMaterial.getMaterial(materialString);
                if (!material.isPresent() || !material.get().isSupported()) {
                    continue top;
                }
                leafMaterials.add(material.get());
            }

            saplingMaterial = CompatibleMaterial.getMaterial(tree.getString("sapling")).get();

            for (String materialString : tree.getStringList("plantable-soil")) {
                Optional<XMaterial> material = CompatibleMaterial.getMaterial(materialString);
                if (!material.isPresent() || !material.get().isSupported()) {
                    continue top;
                }
                plantableSoilMaterial.add(material.get());
            }

            maxLogDistanceFromTrunk = tree.getDouble("max-log-distance-from-trunk");
            maxLeafDistanceFromLog = tree.getInt("max-leaf-distance-from-log");
            detectLeavesDiagonally = tree.getBoolean("search-for-leaves-diagonally");
            dropOriginalLog = tree.getBoolean("drop-original-log");
            dropOriginalLeaf = tree.getBoolean("drop-original-leaf");

            ConfigurationSection logLootSection = tree.getConfigurationSection("log-loot");
            if (logLootSection != null) {
                for (String lootKey : logLootSection.getKeys(false)) {
                    logLoot.add(this.getTreeLootEntry(TreeBlockType.LOG, logLootSection.getConfigurationSection(lootKey)));
                }
            }

            ConfigurationSection leafLootSection = tree.getConfigurationSection("leaf-loot");
            if (leafLootSection != null) {
                for (String lootKey : leafLootSection.getKeys(false)) {
                    leafLoot.add(this.getTreeLootEntry(TreeBlockType.LEAF, leafLootSection.getConfigurationSection(lootKey)));
                }
            }

            ConfigurationSection entireTreeLootSection = tree.getConfigurationSection("entire-tree-loot");
            if (entireTreeLootSection != null) {
                for (String lootKey : entireTreeLootSection.getKeys(false)) {
                    entireTreeLoot.add(this.getTreeLootEntry(TreeBlockType.LEAF, entireTreeLootSection.getConfigurationSection(lootKey)));
                }
            }

            for (String itemStackString : tree.getStringList("required-tools")) {
                Optional<XMaterial> material = CompatibleMaterial.getMaterial(itemStackString);
                if (!material.isPresent()) {
                    continue top;
                }
                requiredTools.add(material.get().parseItem());
            }

            requiredAxe = tree.getBoolean("required-axe", false);

            this.treeDefinitions.add(new TreeDefinition(key, logMaterials, leafMaterials, saplingMaterial, plantableSoilMaterial, maxLogDistanceFromTrunk,
                    maxLeafDistanceFromLog, detectLeavesDiagonally, dropOriginalLog, dropOriginalLeaf, logLoot, leafLoot, entireTreeLoot, requiredTools, requiredAxe));
        }

        // Load global plantable soil
        for (String material : config.getStringList("global-plantable-soil")) {
            this.globalPlantableSoil.add(CompatibleMaterial.getMaterial(material).get());
        }

        // Load global log drops
        ConfigurationSection logSection = config.getConfigurationSection("global-log-loot");
        if (logSection != null) {
            for (String lootKey : logSection.getKeys(false)) {
                this.globalLogLoot.add(this.getTreeLootEntry(TreeBlockType.LOG, logSection.getConfigurationSection(lootKey)));
            }
        }

        // Load global leaf drops
        ConfigurationSection leafSection = config.getConfigurationSection("global-leaf-loot");
        if (leafSection != null) {
            for (String lootKey : leafSection.getKeys(false)) {
                this.globalLeafLoot.add(this.getTreeLootEntry(TreeBlockType.LEAF, leafSection.getConfigurationSection(lootKey)));
            }
        }

        // Load global entire tree drops
        ConfigurationSection entireTreeSection = config.getConfigurationSection("global-entire-tree-loot");
        if (entireTreeSection != null) {
            for (String lootKey : entireTreeSection.getKeys(false)) {
                this.globalEntireTreeLoot.add(this.getTreeLootEntry(TreeBlockType.LOG, entireTreeSection.getConfigurationSection(lootKey)));
            }
        }

        // Load global tools
        for (String itemStackString : config.getStringList("global-required-tools")) {
            Optional<XMaterial> tool = CompatibleMaterial.getMaterial(itemStackString);
            if (!tool.isPresent()) {
                continue;
            }
            this.globalRequiredTools.add(tool.get().parseItem());
        }

        this.globalAxeRequired = config.getBoolean("global-required-axe", false);

        // Load required axe
        if (config.contains("required-axe")) {
            loadAxe(config);
        }
    }

    private void loadAxe(YamlConfiguration config) {
        // Reset the axe loaded, but load the NBT anyway in case someone wanted to use another plugin to give it.
        this.requiredAxeKey = config.getString("required-axe.nbt");
        this.requiredAxe = null;

        String typeString = config.getString("required-axe.type");

        if (Strings.isNullOrEmpty(typeString)) {
            this.plugin.getLogger().warning("Required-axe has to have a material set.");
            return;
        }

        Optional<XMaterial> material = CompatibleMaterial.getMaterial(typeString);
        if (!material.isPresent()) {
            this.plugin.getLogger().warning("Material " + typeString + " is invalid.");
            return;
        }

        ItemStack item = material.get().parseItem();

        // Add display name and lore
        String displayName = TextUtils.formatText(config.getString("required-axe.name"));
        List<String> lore = config.getStringList("required-axe.lore").stream()
                .map(TextUtils::formatText)
                .collect(Collectors.toList());

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);

        // Enchants
        for (String enchantString : config.getStringList("required-axe.enchants")) {
            String[] arr = enchantString.split(":");
            int level = arr.length > 1 ? Math.max(1, parseInt(arr[1])) : 1;

            // Enchantment#getKey is not present on versions below 1.13
            Enchantment enchantment;
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                NamespacedKey key = NamespacedKey.minecraft(arr[0].trim().toLowerCase());
                enchantment = Enchantment.getByKey(key);

                // Try to fall back to #getByName() if someone uses the old names.
                if (enchantment == null) {
                    enchantment = Enchantment.getByName(arr[0].trim());
                }
            } else {
                enchantment = Enchantment.getByName(arr[0].trim());
            }

            if (enchantment == null) {
                this.plugin.getLogger().warning("Enchantment " + arr[0].trim() + " is invalid.");
                continue;
            }

            meta.addEnchant(enchantment, level, true);
        }

        item.setItemMeta(meta);

        // Apply NBT
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean(this.requiredAxeKey, true);
        item = nbtItem.getItem();

        this.requiredAxe = item;
    }

    private int parseInt(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public ItemStack getRequiredAxe() {
        return this.requiredAxe;
    }

    public boolean isGlobalAxeRequired() {
        return this.globalAxeRequired;
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
     * @param block                   The Block to narrow to
     * @param treeBlockType           The TreeBlockType of the given Block
     * @return A Set of TreeDefinitions narrowed down
     */
    public Set<TreeDefinition> narrowTreeDefinition(Set<TreeDefinition> possibleTreeDefinitions, Block block, TreeBlockType treeBlockType) {
        Set<TreeDefinition> matchingTreeDefinitions = new HashSet<>();
        switch (treeBlockType) {
            case LOG:
                for (TreeDefinition treeDefinition : possibleTreeDefinitions) {
                    for (XMaterial material : treeDefinition.getLogMaterial()) {
                        if (material == CompatibleMaterial.getMaterial(block.getType()).orElse(null)) {
                            matchingTreeDefinitions.add(treeDefinition);
                            break;
                        }
                    }
                }
                break;
            case LEAF:
                for (TreeDefinition treeDefinition : possibleTreeDefinitions) {
                    for (XMaterial material : treeDefinition.getLeafMaterial()) {
                        if (material == CompatibleMaterial.getMaterial(block.getType()).orElse(null)) {
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
        if (ConfigurationManager.Setting.IGNORE_REQUIRED_TOOLS.getBoolean()) {
            return true;
        }

        for (TreeDefinition treeDefinition : this.treeDefinitions) {
            if (treeDefinition.isRequiredAxe() || isGlobalAxeRequired()) {
                if (tool != null && !tool.getType().isAir() && new NBTItem(tool).hasTag(this.requiredAxeKey)) {
                    return true;
                }
            }
        }

        for (TreeDefinition treeDefinition : this.treeDefinitions) {
            for (ItemStack requiredTool : treeDefinition.getRequiredTools()) {
                if (tool != null && requiredTool.getType() == tool.getType()) {
                    return true;
                }
            }
        }

        for (ItemStack requiredTool : this.globalRequiredTools) {
            if (tool != null && requiredTool.getType() == tool.getType()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a given tool is valid for a given tree definition, also takes into account global tools
     *
     * @param treeDefinition The TreeDefinition to use
     * @param tool           The tool to check
     * @return True if the tool is allowed for toppling the given TreeDefinition
     */
    public boolean isToolValidForTreeDefinition(TreeDefinition treeDefinition, ItemStack tool) {
        if (ConfigurationManager.Setting.IGNORE_REQUIRED_TOOLS.getBoolean()) {
            return true;
        }

        // If the tree definition requires the custom axe, don't allow any other checks to pass.
        if (treeDefinition.isRequiredAxe() || isGlobalAxeRequired()) {
            return tool != null && !tool.getType().isAir() && new NBTItem(tool).hasTag(this.requiredAxeKey);
        }

        for (ItemStack requiredTool : treeDefinition.getRequiredTools()) {
            if (requiredTool.getType() == tool.getType()) {
                return true;
            }
        }

        for (ItemStack requiredTool : this.globalRequiredTools) {
            if (requiredTool.getType() == tool.getType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tries to spawn loot for a given TreeBlock with the given TreeDefinition for a given Player
     *
     * @param treeDefinition  The TreeDefinition to use
     * @param treeBlock       The TreeBlock to drop for
     * @param player          The Player to drop for
     * @param isForEntireTree If the loot is for the entire tree
     */
    public void dropTreeLoot(TreeDefinition treeDefinition, ITreeBlock treeBlock, Player player, boolean hasSilkTouch, boolean isForEntireTree) {
        boolean addToInventory = ConfigurationManager.Setting.ADD_ITEMS_TO_INVENTORY.getBoolean();
        boolean hasBonusChance = player.hasPermission("ultimatetimber.bonusloot");
        List<ItemStack> lootedItems = new ArrayList<>();
        List<String> lootedCommands = new ArrayList<>();

        // Get the loot that we should try to drop
        List<TreeLoot> toTry = new ArrayList<>();
        if (isForEntireTree) {
            toTry.addAll(treeDefinition.getEntireTreeLoot());
            toTry.addAll(this.globalEntireTreeLoot);
        } else {
            if (ConfigurationManager.Setting.APPLY_SILK_TOUCH.getBoolean() && hasSilkTouch) {
                if (ConfigurationManager.Setting.HOOKS_APPLY_EXTRA_DROPS.getBoolean()
                        && McMMOHook.hasWoodcuttingDoubleDrops(player)) {
                    lootedItems.addAll(BlockUtils.getBlockDrops(treeBlock));
                }
                lootedItems.addAll(BlockUtils.getBlockDrops(treeBlock));
            } else {
                switch (treeBlock.getTreeBlockType()) {
                    case LOG:
                        toTry.addAll(treeDefinition.getLogLoot());
                        toTry.addAll(this.globalLogLoot);
                        if (treeDefinition.shouldDropOriginalLog()) {
                            if (ConfigurationManager.Setting.HOOKS_APPLY_EXTRA_DROPS.getBoolean()
                                    && McMMOHook.hasWoodcuttingDoubleDrops(player)) {
                                lootedItems.addAll(BlockUtils.getBlockDrops(treeBlock));
                            }
                            lootedItems.addAll(BlockUtils.getBlockDrops(treeBlock));
                        }
                        break;
                    case LEAF:
                        toTry.addAll(treeDefinition.getLeafLoot());
                        toTry.addAll(this.globalLeafLoot);
                        if (treeDefinition.shouldDropOriginalLeaf()) {
                            if (ConfigurationManager.Setting.HOOKS_APPLY_EXTRA_DROPS.getBoolean()
                                    && McMMOHook.hasWoodcuttingDoubleDrops(player)) {
                                lootedItems.addAll(BlockUtils.getBlockDrops(treeBlock));
                            }
                            lootedItems.addAll(BlockUtils.getBlockDrops(treeBlock));
                        }
                        break;
                }
            }
        }

        // Roll the dice
        double bonusLootMultiplier = ConfigurationManager.Setting.BONUS_LOOT_MULTIPLIER.getDouble();
        for (TreeLoot treeLoot : toTry) {
            if (treeLoot == null) {
                continue;
            }
            double chance = hasBonusChance ? treeLoot.getChance() * bonusLootMultiplier : treeLoot.getChance();
            if (this.random.nextDouble() > chance / 100) {
                continue;
            }

            if (treeLoot.hasItem()) {
                if (ConfigurationManager.Setting.HOOKS_APPLY_EXTRA_DROPS.getBoolean()
                        && McMMOHook.hasWoodcuttingDoubleDrops(player)) {
                    lootedItems.add(treeLoot.getItem());
                }
                lootedItems.add(treeLoot.getItem());
            }

            if (treeLoot.hasCommand()) {
                if (ConfigurationManager.Setting.HOOKS_APPLY_EXTRA_DROPS.getBoolean()
                        && McMMOHook.hasWoodcuttingDoubleDrops(player)) {
                    lootedCommands.add(treeLoot.getCommand());
                }
                lootedCommands.add(treeLoot.getCommand());
            }
        }

        // Add to inventory or drop on ground
        if (addToInventory && player.getWorld().equals(treeBlock.getLocation().getWorld())) {
            List<ItemStack> extraItems = new ArrayList<>();
            for (ItemStack lootedItem : lootedItems) {
                extraItems.addAll(player.getInventory().addItem(lootedItem).values());
            }
            Location location = player.getLocation().clone().subtract(0.5, 0, 0.5);
            for (ItemStack extraItem : extraItems) {
                location.getWorld().dropItemNaturally(location, extraItem);
            }
        } else {
            Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
            for (ItemStack lootedItem : lootedItems) {
                location.getWorld().dropItemNaturally(location, lootedItem);
            }
        }

        // Run looted commands
        for (String lootedCommand : lootedCommands) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    lootedCommand.replace("%player%", player.getName())
                            .replace("%type%", treeDefinition.getKey())
                            .replace("%xPos%", String.valueOf(treeBlock.getLocation().getBlockX()))
                            .replace("%yPos%", String.valueOf(treeBlock.getLocation().getBlockY()))
                            .replace("%zPos%", String.valueOf(treeBlock.getLocation().getBlockZ())));
        }
    }

    /**
     * Gets all possible plantable soil blocks for the given tree definition
     *
     * @param treeDefinition The TreeDefinition
     * @return A Set of IBlockData of plantable soil
     */
    public Set<XMaterial> getPlantableSoilMaterial(TreeDefinition treeDefinition) {
        Set<XMaterial> plantableSoilBlockData = new HashSet<>();
        plantableSoilBlockData.addAll(treeDefinition.getPlantableSoilMaterial());
        plantableSoilBlockData.addAll(this.globalPlantableSoil);
        return plantableSoilBlockData;
    }

    /**
     * Gets a TreeLoot entry from a ConfigurationSection
     *
     * @param treeBlockType        The TreeBlockType to use
     * @param configurationSection The ConfigurationSection
     * @return A TreeLoot entry from the section
     */
    private TreeLoot getTreeLootEntry(TreeBlockType treeBlockType, ConfigurationSection configurationSection) {
        String material = configurationSection.getString("material");
        Optional<XMaterial> compatibleMaterial = material == null ? Optional.empty() : CompatibleMaterial.getMaterial(material);

        ItemStack item = compatibleMaterial.map(XMaterial::parseItem).orElse(null);
        String command = configurationSection.getString("command");
        double chance = configurationSection.getDouble("chance");
        return new TreeLoot(treeBlockType, item, command, chance);
    }
}
