package com.craftaro.ultimatetimber.managers;

import com.craftaro.core.compatibility.CompatibleHand;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.world.SItemStack;
import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.ultimatetimber.misc.OnlyToppleWhile;
import com.craftaro.ultimatetimber.tree.DetectedTree;
import com.craftaro.ultimatetimber.tree.TreeBlockSet;
import com.craftaro.ultimatetimber.events.TreePostFallEvent;
import com.craftaro.ultimatetimber.events.TreePreFallEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TreeFallManager implements Listener {
    private final UltimateTimber plugin;

    public TreeFallManager(UltimateTimber plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        TreeDefinitionManager treeDefinitionManager = this.plugin.getTreeDefinitionManager();
        TreeDetectionManager treeDetectionManager = this.plugin.getTreeDetectionManager();
        TreeAnimationManager treeAnimationManager = this.plugin.getTreeAnimationManager();
        SaplingManager saplingManager = this.plugin.getSaplingManager();

        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = CompatibleHand.getHand(event).getItem(player);

        // Protect saplings
        if (saplingManager.isSaplingProtected(block)) {
            event.setCancelled(true);
            return;
        }

        // Condition checks
        boolean isValid = true;

        if (plugin.getMainConfig().getStringList("Settings.Disabled Worlds").contains(player.getWorld().getName()))
            isValid = false;

        if (!plugin.getMainConfig().getBoolean("Settings.Allow Creative Mode") && player.getGameMode() == GameMode.CREATIVE)
            isValid = false;

        if (!this.checkToppleWhile(player))
            isValid = false;

        if (plugin.getMainConfig().getBoolean("Settings.Require Chop Permission") && !player.hasPermission("ultimatetimber.chop"))
            isValid = false;

        if (!plugin.getPlayerManager().canTopple(player))
            isValid = false;

        if (treeAnimationManager.isBlockInAnimation(block)) {
            isValid = false;
            event.setCancelled(true);
        }

        if (!treeDefinitionManager.isToolValidForAnyTreeDefinition(tool))
            isValid = false;

        boolean alwaysReplantSapling = plugin.getMainConfig().getBoolean("Settings.Always Replant Sapling");
        if (!isValid && !alwaysReplantSapling)
            return;

        DetectedTree detectedTree = treeDetectionManager.detectTree(block);
        if (detectedTree == null)
            return;

        if (alwaysReplantSapling) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () ->
                    saplingManager.replantSapling(detectedTree.getTreeDefinition(), detectedTree.getDetectedTreeBlocks().getInitialLogBlock()));

            if (!isValid)
                return;
        }

        if (!treeDefinitionManager.isToolValidForTreeDefinition(detectedTree.getTreeDefinition(), tool))
            return;

        short toolDamage = this.getToolDamage(detectedTree.getDetectedTreeBlocks(), tool.containsEnchantment(Enchantment.SILK_TOUCH));
        if (plugin.getMainConfig().getBoolean("Settings.Protect Tool") && !ItemUtils.hasEnoughDurability(tool, toolDamage)) {
            return;
        }

        // Trigger fall event
        TreePreFallEvent treeFallEvent = new TreePreFallEvent(player, detectedTree);
        Bukkit.getPluginManager().callEvent(treeFallEvent);
        if (treeFallEvent.isCancelled())
            return;

        // Valid tree and meets all conditions past this point
        event.setCancelled(true);

        detectedTree.getDetectedTreeBlocks().sortAndLimit(plugin.getMainConfig().getInt("Settings.Max Logs Per Chop"));
        plugin.getPlayerManager().cooldownPlayer(player);

        // Destroy initiated block if enabled
        if (plugin.getMainConfig().getBoolean("Settings.Destroy Initiated Block")) {
            detectedTree.getDetectedTreeBlocks().getInitialLogBlock().getBlock().setType(Material.AIR);
            detectedTree.getDetectedTreeBlocks().remove(detectedTree.getDetectedTreeBlocks().getInitialLogBlock());
        }


        boolean isCreative = player.getGameMode().equals(GameMode.CREATIVE);
        if (!isCreative) {
            new SItemStack(tool).addDamage(player, toolDamage, true);
        }

        // TODO: Add Jobs & mcMMO hook back here

        treeAnimationManager.runAnimation(detectedTree, player);
        treeDefinitionManager.dropTreeLoot(detectedTree.getTreeDefinition(), detectedTree.getDetectedTreeBlocks().getInitialLogBlock(), player, false, true);

        // Trigger fell event
        TreePostFallEvent treeFellEvent = new TreePostFallEvent(player, detectedTree);
        Bukkit.getPluginManager().callEvent(treeFellEvent);
    }

    /**
     * Checks if a player is doing a certain action required to topple a tree
     *
     * @param player The player to check
     *
     * @return True if the check passes, otherwise false
     */
    private boolean checkToppleWhile(Player player) {
        switch (OnlyToppleWhile.fromString(plugin.getMainConfig().getString("Settings.Only Topple While"))) {
            case SNEAKING:
                return player.isSneaking();
            case NOT_SNEAKING:
                return !player.isSneaking();
            default:
                return true;
        }
    }

    private short getToolDamage(TreeBlockSet<Block> treeBlocks, boolean hasSilkTouch) {
        if (!plugin.getMainConfig().getBoolean("Settings.Realistic Tool Damage"))
            return 1;

        if (plugin.getMainConfig().getBoolean("Settings.Apply Silk Touch Tool Damage") && hasSilkTouch) {
            return (short) treeBlocks.size();
        } else {
            return (short) treeBlocks.getLogBlocks().size();
        }
    }
}