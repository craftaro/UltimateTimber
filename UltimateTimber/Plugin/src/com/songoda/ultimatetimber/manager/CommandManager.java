package com.songoda.ultimatetimber.manager;

import com.songoda.core.locale.Locale;
import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandManager extends Manager implements CommandExecutor, TabCompleter {

    public CommandManager(UltimateTimber plugin) {
        super(plugin);

        PluginCommand command = plugin.getCommand("ultimatetimber");
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
        Locale locale = this.plugin.getLocale();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (commandSender instanceof Player && this.doesntHavePermission(commandSender, "ultimatetimber.reload", locale))
                    return true;

                UltimateTimber.getInstance().reload();
                locale.getMessage("command.reload.reloaded").sendPrefixedMessage(commandSender);
                return true;
            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(Methods.formatText("&cConsole cannot toggle chopping mode!"));
                    return true;
                }

                if (this.doesntHavePermission(commandSender, "ultimatetimber.toggle", locale))
                    return true;

                if (UltimateTimber.getInstance().getChoppingManager().togglePlayer((Player) commandSender)) {
                    locale.getMessage("command.toggle.enabled").sendPrefixedMessage(commandSender);
                } else {
                    locale.getMessage("command.toggle.disabled").sendPrefixedMessage(commandSender);
                }

                return true;
            }
        }

        commandSender.sendMessage("");
        locale.newMessage("&7Version " + plugin.getDescription().getVersion()
                + " Created with <3 by &5&l&oSongoda").sendPrefixedMessage(commandSender);
        locale.getMessage("command.reload.description").sendMessage(commandSender);
        locale.getMessage("command.toggle.description").sendMessage(commandSender);
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
     * Checks if a player does have a permission
     * Sends them an error message if they don't
     *
     * @param sender     The CommandSender to check
     * @param permission The permission to check for
     * @param locale     The LocaleManager instance
     * @return True if the player has permission, otherwise false and sends a message
     */
    private boolean doesntHavePermission(CommandSender sender, String permission, Locale locale) {
        if (!sender.hasPermission(permission)) {
            locale.getMessage("general.nopermission").sendPrefixedMessage(sender);
            return true;
        }
        return false;
    }

}
