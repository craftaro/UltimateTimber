package com.songoda.ultimatetimber.commands;

import org.bukkit.command.CommandSender;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.old_code.CustomLoot;
import com.songoda.ultimatetimber.utils.Methods;

class ReloadCommand {

    static void reloadConfig(CommandSender commandSender) {
        UltimateTimber plugin = UltimateTimber.getInstance();
        plugin.reloadConfig();
        plugin.reloadValidWorlds();
        CustomLoot.initializeCustomItems();
        commandSender.sendMessage(Methods.formatText(plugin.getPrefix() + " &7Configuration reloaded"));
    }

}
