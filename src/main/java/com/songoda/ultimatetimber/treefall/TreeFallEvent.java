package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashSet;

public class TreeFallEvent implements Listener {

    /*
    This is the starting point for the whole effect
    It's been broken up instead of chained in order to make step-by-step debugging easier
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTreeBreak(BlockBreakEvent event) {

        FileConfiguration fileConfiguration = UltimateTimber.getInstance().getConfig();

        if (event.getBlock() != null && event.getBlock().getType().name().contains("SAPLING") &&
                fileConfiguration.getBoolean(DefaultConfig.TIMEOUT_BREAK) && TreeReplant.isTimeout(event.getBlock()))
                event.setCancelled(true);

        if (!EventFilter.eventIsValid(event)) return;
        TreeChecker treeChecker = new TreeChecker();
        HashSet<Block> blocks = treeChecker.validTreeHandler(event.getBlock());


        /*
        Previous list will be null if no valid tree is found
         */
        if (blocks == null)
            return;

        /*
        Everything beyond this point assumes that the tree was valid
         */

        if (fileConfiguration.getBoolean(DefaultConfig.ACCURATE_AXE_DURABILITY))
            AxeDurability.adjustAxeDamage(blocks, event.getPlayer());
        if (fileConfiguration.getBoolean(DefaultConfig.CUSTOM_AUDIO))
            TreeSounds.tipOverNoise(event.getBlock().getLocation());

        if (fileConfiguration.getBoolean(DefaultConfig.SHOW_ANIMATION)) {
            TreeFallAnimation treeFallAnimation = new TreeFallAnimation();
            treeFallAnimation.startAnimation(event.getBlock(), blocks, event.getPlayer());
        } else {
            NoAnimationTreeDestroyer.destroyTree(blocks, event.getPlayer().hasPermission("ultimatetimber.bonusloot"),
                    event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH));
        }

    }

}
