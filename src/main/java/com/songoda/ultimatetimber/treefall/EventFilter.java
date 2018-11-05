package com.songoda.ultimatetimber.treefall;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.configurations.DefaultConfig;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class EventFilter {

    /*
    Incorporate all checks that would disqualify this event from happening
    Mostly config settings, also permissions
     */
    public static boolean eventIsValid(BlockBreakEvent event) {
        /*
        General catchers
         */
        if (event.isCancelled()) return false;

        if (!UltimateTimber.validWorlds.contains(event.getPlayer().getWorld())) return false;

        if (!TreeChecker.validMaterials.contains(event.getBlock().getType())) return false;
        /*
        Config-based catchers
         */
        if (DefaultConfig.configuration.getBoolean(DefaultConfig.CREATIVE_DISALLOWED) &&
                event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return false;

        if (DefaultConfig.configuration.getBoolean(DefaultConfig.AXES_ONLY) &&
                !(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) ||
                        event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE)))
            return false;

        if (DefaultConfig.configuration.getBoolean(DefaultConfig.PERMISSIONS_ONLY) &&
                !event.getPlayer().hasPermission("ultimatetimber.chop"))
            return false;

        return true;

    }

}
