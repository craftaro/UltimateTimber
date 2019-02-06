package com.songoda.ultimatetimber.commands;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private final UltimateTimber plugin;

    public CommandHandler(UltimateTimber plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length > 0)
            if (args[0].equalsIgnoreCase("reload")) {
                if (commandSender instanceof Player && !permCheck((Player) commandSender, "ultimatetimber.reload")) {
                    return true;
                }
                ReloadCommand.reloadConfig(commandSender);
                return true;
            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (commandSender instanceof Player) {
                    if (!permCheck((Player) commandSender, "ultimatetimber.toggle")) {
                        return true;
                    }
                    ToggleCommand.toggleChopping((Player) commandSender);
                    return true;
                }
                commandSender.sendMessage(Methods.formatText("&cConsole cannot toggle chopping mode!"));
                return true;
            }

        commandSender.sendMessage("");
        commandSender.sendMessage(Methods.formatText(plugin.getPrefix() + " &7Version " + plugin.getDescription().getVersion() + " Created with <3 by &5&l&oSongoda"));
        commandSender.sendMessage(Methods.formatText("&8 - &a/ut reload &7 - Reloads the config."));
        commandSender.sendMessage(Methods.formatText("&8 - &a/ut toggle &7 - Toggles your chopping mode"));
        commandSender.sendMessage("");

        return true;
    }

    private boolean permCheck(Player sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Methods.formatText("&cYou don't have permission for that!"));
            return false;
        }
        return true;
    }


}
