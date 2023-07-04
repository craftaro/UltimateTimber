package com.songoda.ultimatetimber.commands;

import com.craftaro.core.commands.AbstractCommand;
import com.songoda.ultimatetimber.UltimateTimber;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandReload extends AbstractCommand {
    private final UltimateTimber plugin;

    public CommandReload(UltimateTimber plugin) {
        super(CommandType.CONSOLE_OK, "reload");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        this.plugin.reloadConfig();
        this.plugin.getLocale().getMessage("command.reload.reloaded").sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatetimber.reload";
    }

    @Override
    public String getSyntax() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return this.plugin.getLocale().getMessage("command.reload.description").getMessage();
    }
}
