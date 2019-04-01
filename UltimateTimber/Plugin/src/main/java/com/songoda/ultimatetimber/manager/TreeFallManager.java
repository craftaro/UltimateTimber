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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
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
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().contains(player.getWorld().getName()))
            return;

        if (!ConfigurationManager.Setting.ALLOW_CREATIVE_MODE.getBoolean() && player.getGameMode().equals(GameMode.CREATIVE))
            return;

        if (ConfigurationManager.Setting.ONLY_TOPPLE_WHILE_SNEAKING.getBoolean() && !player.isSneaking())
            return;

        if (ConfigurationManager.Setting.REQUIRE_CHOP_PERMISSION.getBoolean() && !player.hasPermission("ultimatetimber.chop"))
            return;

        if (!choppingManager.isChopping(player))
            return;

        if (treeAnimationManager.isBlockInAnimation(block))
            return;

        if (!treeDefinitionManager.isToolValidForAnyTreeDefinition(tool))
            return;

        DetectedTree detectedTree = treeDetectionManager.detectTree(block);
        if (detectedTree == null)
            return;

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
