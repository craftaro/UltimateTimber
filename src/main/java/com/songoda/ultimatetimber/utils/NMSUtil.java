package com.songoda.ultimatetimber.utils;

import org.bukkit.Bukkit;

public class NMSUtil {

    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1) + ".";
    }

    public static int getVersionNumber() {
        String name = getVersion().substring(3);
        return Integer.valueOf(name.substring(0, name.length() - 4));
    }

    public static int getVersionReleaseNumber() {
        String NMSVersion = getVersion();
        return Integer.valueOf(NMSVersion.substring(NMSVersion.length() - 2).replace(".", ""));
    }

}
