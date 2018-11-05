package com.songoda.ultimatetimber.commands;

import com.songoda.ultimatetimber.UltimateTimber;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

    public static void reloadConfig(CommandSender commandSender) {

        UltimateTimber.plugin.reloadConfig();
        commandSender.sendMessage("[UltimateTimber] - Configuration reloaded");


    }

}
