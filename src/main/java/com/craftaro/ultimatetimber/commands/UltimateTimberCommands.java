package com.craftaro.ultimatetimber.commands;

import com.craftaro.ultimatetimber.UltimateTimber;
import com.craftaro.core.third_party.revxrsal.commands.annotation.Command;
import com.craftaro.core.third_party.revxrsal.commands.annotation.DefaultFor;
import com.craftaro.core.third_party.revxrsal.commands.annotation.Named;
import com.craftaro.core.third_party.revxrsal.commands.annotation.Subcommand;
import com.craftaro.core.third_party.revxrsal.commands.bukkit.annotation.CommandPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command({"ut", "ultimatetimber"})
public class UltimateTimberCommands {

    private final UltimateTimber plugin;
    public UltimateTimberCommands(UltimateTimber plugin) {
        this.plugin = plugin;
    }

    @DefaultFor({"ut", "ultimatetimber"})
    public void onHelp(CommandSender sender) {
        plugin.getLocaleManager().getMessageList("command.help").sendMessage(sender);
    }

    @Subcommand("giveaxe")
    @CommandPermission("ultimatetimber.give")
    public void onGiveAxe(CommandSender sender, @Named("player") Player player) {
        ItemStack axe = plugin.getTreeDefinitionManager().getRequiredAxe();

        if (axe == null) {
            plugin.getLocaleManager().getMessage("command.give.no-axe").sendMessage(sender);
            return;
        }

        player.getInventory().addItem(axe);
        plugin.getLocaleManager().getMessage("command.give.given")
                .replace("%player%", player.getName())
                .sendMessage(sender);
    }

    @Subcommand("toggle")
    @CommandPermission("ultimatetimber.toggle")
    public void onToggle(Player player) {
        if (plugin.getPlayerManager().togglePlayer(player)) {
            plugin.getLocaleManager().getMessage("command.toggle.enabled").sendMessage(player);
        } else {
            plugin.getLocaleManager().getMessage("command.toggle.disabled").sendMessage(player);
        }
    }

    @Subcommand("reload")
    @CommandPermission("ultimatetimber.reload")
    public void onReload(CommandSender sender) {
        plugin.reloadPlugin();
        plugin.getLocaleManager().getMessage("command.reload.success").sendMessage(sender);
    }
}
