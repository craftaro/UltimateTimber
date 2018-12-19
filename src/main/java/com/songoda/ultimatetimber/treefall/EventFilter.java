package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.BlockBreakEvent;

class EventFilter {

    /*
    Incorporate all checks that would disqualify this event from happening
    Mostly config settings, also permissions
     */
    static boolean eventIsValid(BlockBreakEvent event) {
        UltimateTimber plugin = UltimateTimber.getInstance();

        /*
        General catchers
         */
        if (event.isCancelled()
            || !plugin.getValidWorlds().contains(event.getPlayer().getWorld())
            || !TreeChecker.validMaterials.contains(event.getBlock().getType())) return false;

        FileConfiguration fileConfiguration = UltimateTimber.getInstance().getConfig();
        
        /*
        Config-based catchers
         */
        if (fileConfiguration.getBoolean(DefaultConfig.CREATIVE_DISALLOWED) &&
                event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return false;

        if (fileConfiguration.getBoolean(DefaultConfig.AXES_ONLY) &&
                !(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE)))
            return false;

        if (fileConfiguration.getBoolean(DefaultConfig.SNEAK_ONLY) &&
                !event.getPlayer().isSneaking())
            return false;

        return !fileConfiguration.getBoolean(DefaultConfig.PERMISSIONS_ONLY) ||
                event.getPlayer().hasPermission("ultimatetimber.chop");

    }

}
