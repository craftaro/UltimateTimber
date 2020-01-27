package com.songoda.ultimatetimber.utils;

import org.bukkit.ChatColor;

import java.util.Random;

public class Methods {

    private static Random random = new Random();

    /**
     * Formats a given string replacing colors
     *
     * @param text The string
     * @return The formatted string
     */
    public static String formatText(String text) {
        if (text == null || text.equals(""))
            return "";
        return formatText(text, false);
    }

    /**
     * Formats a given string replacing colors and optionally capitalizing the first word
     *
     * @param text The string
     * @param cap If the first word should be capitalized
     * @return The formatted string
     */
    private static String formatText(String text, boolean cap) {
        if (text == null || text.equals(""))
            return "";
        if (cap)
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Check if durbility should be applied based on the unbreaking enchantment
     *
     * @param level The level of the unbreaking enchantment
     * @return True if durability should be applied, otherwise false
     */
    public static boolean checkUnbreakingChance(int level) {
        return (1.0 / (level + 1)) > random.nextDouble();
    }

}
