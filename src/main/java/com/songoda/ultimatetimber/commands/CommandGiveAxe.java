package com.songoda.ultimatetimber.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.utils.PlayerUtils;
import com.songoda.ultimatetimber.UltimateTimber;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandGiveAxe extends AbstractCommand {
    private final UltimateTimber plugin;

    public CommandGiveAxe(UltimateTimber plugin) {
        super(CommandType.CONSOLE_OK, true, "give");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 1) {
            return ReturnType.SYNTAX_ERROR;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            if (args[0].trim().equalsIgnoreCase("me")) {
                if (!(sender instanceof Player)) {
                    return ReturnType.NEEDS_PLAYER;
                }
                player = (Player) sender;
            } else {
                this.plugin.getLocale().getMessageOrDefault("command.give.not-a-player", "&cNot a player.")
                        .sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }
        }

        ItemStack axe = this.plugin.getTreeDefinitionManager().getRequiredAxe();

        if (axe == null) {
            this.plugin.getLocale().getMessageOrDefault("command.give.no-axe", "&cThe axe could not be loaded.")
                    .sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        player.getInventory().addItem(axe);
        this.plugin.getLocale().getMessageOrDefault("command.give.given", "&fAxe given to &a%player%")
                .processPlaceholder("player", player.getName())
                .sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        List<String> suggestions = null;
        if (args.length == 1) {
            suggestions = PlayerUtils.getVisiblePlayerNames(sender, args[0]);
            suggestions.add("me");
        }
        return suggestions;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatetimber.give";
    }

    @Override
    public String getSyntax() {
        return "give <player/me>";
    }

    @Override
    public String getDescription() {
        return "Give a required axe.";
    }
}
