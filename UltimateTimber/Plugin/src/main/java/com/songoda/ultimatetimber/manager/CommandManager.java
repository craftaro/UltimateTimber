package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandManager extends Manager implements CommandExecutor, TabCompleter {

    public CommandManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);

        PluginCommand command = ultimateTimber.getCommand("ultimatetimber");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        LocaleManager localeManager = this.ultimateTimber.getLocaleManager();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (commandSender instanceof Player && this.doesntHavePermission(commandSender, "ultimatetimber.reload", localeManager))
                    return true;

                UltimateTimber.getInstance().reload();
                localeManager.sendPrefixedMessage(commandSender, LocaleManager.Locale.COMMAND_RELOAD_RELOADED);
                return true;
            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(Methods.formatText("&cConsole cannot toggle chopping mode!"));
                    return true;
                }

                if (this.doesntHavePermission(commandSender, "ultimatetimber.toggle", localeManager))
                    return true;

                if (UltimateTimber.getInstance().getChoppingManager().togglePlayer((Player) commandSender)) {
                    localeManager.sendPrefixedMessage(commandSender, LocaleManager.Locale.COMMAND_TOGGLE_ENABLED);
                } else {
                    localeManager.sendPrefixedMessage(commandSender, LocaleManager.Locale.COMMAND_TOGGLE_DISABLED);
                }

                return true;
            }
        }

        commandSender.sendMessage("");
        commandSender.sendMessage(Methods.formatText(LocaleManager.Locale.PREFIX.get() + " &7Version " + UltimateTimber.getInstance().getDescription().getVersion() + " Created with <3 by &5&l&oSongoda"));
        localeManager.sendMessage(commandSender, LocaleManager.Locale.COMMAND_RELOAD_DESCRIPTION);
        localeManager.sendMessage(commandSender, LocaleManager.Locale.COMMAND_TOGGLE_DESCRIPTION);
        commandSender.sendMessage("");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length < 1)
            return completions;

        Set<String> possibleCompletions = new HashSet<>();

        if (commandSender.hasPermission("ultimatetimber.reload"))
            possibleCompletions.add("reload");

        if (commandSender.hasPermission("ultimatetimber.toggle") && commandSender instanceof Player)
            possibleCompletions.add("toggle");

        StringUtil.copyPartialMatches(args[0], possibleCompletions, completions);

        return completions;
    }

    /**
     * Checks if a player has a permission
     *
     * @param sender The CommandSender to check
     * @param permission The permission to check for
     * @param localeManager The LocaleManager instance
     * @return True if the player has permission, otherwise false and sends a message
     */
    private boolean doesntHavePermission(CommandSender sender, String permission, LocaleManager localeManager) {
        if (!sender.hasPermission(permission)) {
            localeManager.sendPrefixedMessage(sender, LocaleManager.Locale.NO_PERMISSION);
            return true;
        }
        return false;
    }

}
