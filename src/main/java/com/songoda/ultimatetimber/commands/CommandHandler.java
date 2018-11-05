package com.songoda.ultimatetimber.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length > 0)
            if (args[0].equalsIgnoreCase("reload")) {
                ReloadCommand.reloadConfig(commandSender);
                return true;
            }

        commandSender.sendMessage("[UltimateTimber] Command usage: /em reload");

        return true;
    }

}
