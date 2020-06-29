package com.songoda.ultimatetimber.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimatetimber.UltimateTimber;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandToggle extends AbstractCommand {

    private final UltimateTimber plugin;

    public CommandToggle(UltimateTimber plugin) {
        super(CommandType.CONSOLE_OK, "toggle");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (UltimateTimber.getInstance().getChoppingManager().togglePlayer((Player) sender)) {
            plugin.getLocale().getMessage("command.toggle.enabled").sendPrefixedMessage(sender);
        } else {
            plugin.getLocale().getMessage("command.toggle.disabled").sendPrefixedMessage(sender);
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatetimber.toggle";
    }

    @Override
    public String getSyntax() {
        return "toggle";
    }

    @Override
    public String getDescription() {
        return plugin.getLocale().getMessage("command.toggle.description").getMessage();
    }

}
