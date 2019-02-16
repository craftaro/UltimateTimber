package com.songoda.ultimatetimber.treefall;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import com.songoda.ultimatetimber.events.TreeFallEvent;
import com.songoda.ultimatetimber.events.TreeFellEvent;
import com.songoda.ultimatetimber.hooks.HookManager;

public class TreeFallListener implements Listener {

    /*
    This is the starting point for the whole effect
    It's been broken up instead of chained in order to make step-by-step debugging easier
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTreeBreak(BlockBreakEvent event) {

        FileConfiguration config = UltimateTimber.getInstance().getConfig();
        
        Block block = event.getBlock();

        if (block != null && block.getType().name().contains("SAPLING") &&
                config.getBoolean(DefaultConfig.TIMEOUT_BREAK) && TreeReplant.isTimeout(block))
            event.setCancelled(true);
        if (!EventFilter.eventIsValid(event)) return;
        if (config.getBoolean(DefaultConfig.SNEAK_ONLY) && !event.getPlayer().isSneaking()) return;

        if (!UltimateTimber.getInstance().isChopping(event.getPlayer())) return;

        TreeChecker treeChecker = new TreeChecker();
        HashSet<Block> blocks = treeChecker.parseTree(block);

        /*
        Previous list will be null if no valid tree is found
         */
        if (blocks == null)
            return;
        
   	  	//Call event that tree will fall
        TreeFallEvent treeFallEvent = new TreeFallEvent(event.getPlayer(), treeChecker, block);
        Bukkit.getPluginManager().callEvent(treeFallEvent);
        if (treeFallEvent.isCancelled()) return;
        
        /*
        Everything beyond this point assumes that the tree was valid
         */
        
        // Do not let any items drop, it will be handled later
        event.setDropItems(false);
        
        // Remove log if it's enabled
        if (config.getBoolean(DefaultConfig.DELETE_BROKEN_LOG))
            TreeReplant.replaceOriginalBlock(block);
        
        // Apply hooks
        HookManager.getInstance().applyHooks(event.getPlayer(), blocks);

        if (config.getBoolean(DefaultConfig.ACCURATE_AXE_DURABILITY))
            AxeDurability.adjustAxeDamage(blocks, event.getPlayer());
        if (config.getBoolean(DefaultConfig.CUSTOM_AUDIO))
            TreeSounds.tipOverNoise(block.getLocation());

        if (config.getBoolean(DefaultConfig.SHOW_ANIMATION)) {
            TreeFallAnimation treeFallAnimation = new TreeFallAnimation();
            treeFallAnimation.startAnimation(block, blocks, event.getPlayer());
        } else {
            NoAnimationTreeDestroyer.destroyTree(blocks, event.getPlayer().hasPermission("ultimatetimber.bonusloot"),
                    event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH));
        }
        
        //Call event that a tree has fell
        TreeFellEvent treeFellEvent = new TreeFellEvent(event.getPlayer(), treeChecker, block);
        Bukkit.getPluginManager().callEvent(treeFellEvent);
        
    }

}
