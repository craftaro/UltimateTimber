package com.songoda.ultimatetimber.commands;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.treefall.CustomLoot;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class ReloadCommand {

    static void reloadConfig(CommandSender commandSender) {

        if (commandSender instanceof Player) {

            if (!commandSender.hasPermission("ut.reload")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission!"));
                return;
            }

        }

        UltimateTimber plugin = UltimateTimber.getInstance();
        plugin.reloadConfig();
        CustomLoot.initializeCustomItems();
        commandSender.sendMessage(Methods.formatText(plugin.getPrefix() + " &7Configuration reloaded"));


    }

}
