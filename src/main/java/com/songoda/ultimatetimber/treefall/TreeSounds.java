package com.songoda.ultimatetimber.treefall;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;

public class TreeSounds {

    public static void tipOverNoise(Location location) {

        if(Bukkit.getServer().getClass().getPackage().toString().contains("8")){
            location.getWorld().playSound(location, Sound.valueOf("CHEST_OPEN"), 3f,0.1f);
        } else {

            location.getWorld().playSound(location, Sound.BLOCK_CHEST_OPEN, 3F, 0.1F);
        }

    }

    public static void fallNoise(FallingBlock fallingBlock) {

        if(Bukkit.getServer().getClass().getPackage().toString().contains("8")){
                fallingBlock.getWorld().playSound(fallingBlock.getLocation(), Sound.valueOf("ANVIL_LAND"), 3F, 0.1F);
            return;
        }

        if (fallingBlock.getTicksLived() < 20)
            fallingBlock.getWorld().playSound(fallingBlock.getLocation(), Sound.BLOCK_ANVIL_FALL, 3F, 0.1F);
        else
            fallingBlock.getWorld().playSound(fallingBlock.getLocation(), Sound.BLOCK_WOOD_FALL, 3F, 0.1F);

    }

}
