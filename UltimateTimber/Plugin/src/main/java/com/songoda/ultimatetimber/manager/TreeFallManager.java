package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.adapter.VersionAdapter;
import com.songoda.ultimatetimber.events.TreeFallEvent;
import com.songoda.ultimatetimber.events.TreeFellEvent;
import com.songoda.ultimatetimber.tree.DetectedTree;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TreeFallManager extends Manager implements Listener {

    public TreeFallManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        Bukkit.getPluginManager().registerEvents(this, ultimateTimber);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        TreeDefinitionManager treeDefinitionManager = this.ultimateTimber.getTreeDefinitionManager();
        TreeDetectionManager treeDetectionManager = this.ultimateTimber.getTreeDetectionManager();
        TreeAnimationManager treeAnimationManager = this.ultimateTimber.getTreeAnimationManager();
        ChoppingManager choppingManager = this.ultimateTimber.getChoppingManager();
        SaplingManager saplingManager = this.ultimateTimber.getSaplingManager();
        VersionAdapter versionAdapter = this.ultimateTimber.getVersionAdapter();
        HookManager hookManager = this.ultimateTimber.getHookManager();

        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = versionAdapter.getItemInHand(player);

        // Protect saplings
        if (saplingManager.isSaplingProtected(block)) {
            event.setCancelled(true);
            return;
        }

        // Condition checks
        boolean isValid = true;

        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().contains(player.getWorld().getName()))
            isValid = false;

        if (!ConfigurationManager.Setting.ALLOW_CREATIVE_MODE.getBoolean() && player.getGameMode().equals(GameMode.CREATIVE))
            isValid = false;

        if (ConfigurationManager.Setting.ONLY_TOPPLE_WHILE_SNEAKING.getBoolean() && !player.isSneaking())
            isValid = false;

        if (ConfigurationManager.Setting.REQUIRE_CHOP_PERMISSION.getBoolean() && !player.hasPermission("ultimatetimber.chop"))
            isValid = false;

        if (!choppingManager.isChopping(player))
            isValid = false;

        if (treeAnimationManager.isBlockInAnimation(block))
            isValid = false;

        if (!treeDefinitionManager.isToolValidForAnyTreeDefinition(tool))
            isValid = false;

        boolean alwaysReplantSapling = ConfigurationManager.Setting.ALWAYS_REPLANT_SAPLING.getBoolean();
        if (!isValid && !alwaysReplantSapling)
            return;

        DetectedTree detectedTree = treeDetectionManager.detectTree(block);
        if (detectedTree == null)
            return;

        if (alwaysReplantSapling) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.ultimateTimber, () ->
                    saplingManager.replantSapling(detectedTree.getTreeDefinition(), detectedTree.getDetectedTreeBlocks().getInitialLogBlock()));

            if (!isValid)
                return;
        }

        if (!treeDefinitionManager.isToolValidForTreeDefinition(detectedTree.getTreeDefinition(), tool))
            return;

        int toolDamage = ConfigurationManager.Setting.REALISTIC_TOOL_DAMAGE.getBoolean() ? detectedTree.getDetectedTreeBlocks().getLogBlocks().size() : 1;
        if (ConfigurationManager.Setting.PROTECT_TOOL.getBoolean() && !versionAdapter.hasEnoughDurability(tool, toolDamage))
            return;

        // Trigger fall event
        TreeFallEvent treeFallEvent = new TreeFallEvent(player, detectedTree);
        Bukkit.getPluginManager().callEvent(treeFallEvent);
        if (treeFallEvent.isCancelled())
            return;

        // Valid tree and meets all conditions past this point
        event.setCancelled(true);

        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            versionAdapter.applyToolDurability(player, toolDamage);
            hookManager.applyHooks(player, detectedTree.getDetectedTreeBlocks());
        }

        treeAnimationManager.runAnimation(detectedTree, player);

        // Trigger fell event
        TreeFellEvent treeFellEvent = new TreeFellEvent(player, detectedTree);
        Bukkit.getPluginManager().callEvent(treeFellEvent);
    }

}
