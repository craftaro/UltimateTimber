package com.songoda.ultimatetimber.commands;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.treefall.CustomLoot;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

    public static void reloadConfig(CommandSender commandSender) {
        UltimateTimber plugin = UltimateTimber.getInstance();
        plugin.reloadConfig();
        CustomLoot.initializeCustomItems();
        commandSender.sendMessage(Methods.formatText(plugin.getPrefix() + " &7Configuration reloaded"));


    }

}
