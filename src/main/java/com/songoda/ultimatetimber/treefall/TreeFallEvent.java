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

import java.util.LinkedHashSet;

public class TreeFallEvent implements Listener {

    /*
    This is the starting point for the whole effect
    It's been broken up instead of chained in order to make step-by-step debugging easier
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTreeBreak(BlockBreakEvent event) {

        if (!EventFilter.eventIsValid(event)) return;
        LinkedHashSet<Block> blocks = TreeChecker.parseTree(event.getBlock(), true);

        /*
        Check if the list of blocks carried over from the tree parser contains everything you'd expect from a tree
         */
        if (blocks == null)
            return;

        boolean containsLeaves = false;

        for (Block block : blocks)
            if (TreeChecker.validTreeMaterials.contains(block.getType())) {
                containsLeaves = true;
                break;
            }

        if (!containsLeaves)
            return;

        FileConfiguration fileConfiguration = UltimateTimber.getInstance().getConfig();


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
