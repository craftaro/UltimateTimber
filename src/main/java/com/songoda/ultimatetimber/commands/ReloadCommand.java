package com.songoda.ultimatetimber.commands;

import com.songoda.ultimatetimber.configurations.DefaultConfig;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

    public static void reloadConfig(CommandSender commandSender) {

        DefaultConfig.initialize();
        commandSender.sendMessage("[UltimateTimber] - Configuration reloaded");


    }

}
