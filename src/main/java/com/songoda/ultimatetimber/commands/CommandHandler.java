package com.songoda.ultimatetimber.commands;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    private final UltimateTimber plugin;

    public CommandHandler(UltimateTimber plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length > 0)
            if (args[0].equalsIgnoreCase("reload")) {
                ReloadCommand.reloadConfig(commandSender);
                return true;
            }

        commandSender.sendMessage("");
        commandSender.sendMessage(Methods.formatText(plugin.getPrefix() + " &7Version " + plugin.getDescription().getVersion() + " Created with <3 by &5&l&oBrianna"));
        commandSender.sendMessage(Methods.formatText("&8 - &a/ut reload &7 - Reloads the config."));
        commandSender.sendMessage("");

        return true;
    }

}
