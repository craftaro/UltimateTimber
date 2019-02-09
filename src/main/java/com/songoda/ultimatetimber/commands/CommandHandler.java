package com.songoda.ultimatetimber.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.utils.Methods;

public class CommandHandler implements CommandExecutor, TabCompleter {

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
        commandSender.sendMessage(Methods.formatText(UltimateTimber.getInstance().getPrefix() + " &7Version " + UltimateTimber.getInstance().getDescription().getVersion() + " Created with <3 by &5&l&oSongoda"));
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

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 0 || args.length > 1) 
            return completions;
        
        Set<String> possibleCompletions = new HashSet<>();
        
        if (commandSender.hasPermission("ultimatetimber.reload")) 
            possibleCompletions.add("reload");
        
        if (commandSender.hasPermission("ultimatetimber.toggle") && commandSender instanceof Player)
            possibleCompletions.add("toggle");
        
        StringUtil.copyPartialMatches(args[0], possibleCompletions, completions);
        
        return completions;
    }

}
