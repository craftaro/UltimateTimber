package com.songoda.ultimatetimber.commands;


import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.utils.Methods;
import org.bukkit.entity.Player;

public class ToggleCommand {

    public static void toggleChopping(Player player) {
        if (UltimateTimber.getInstance().toggleChopping(player)) {
            player.sendMessage(Methods.formatText("UT Chopping Mode: &aEnabled"));
        } else {
            player.sendMessage(Methods.formatText("UT Chopping Mode: &cDisabled"));
        }
    }

}
