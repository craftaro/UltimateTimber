package com.songoda.ultimatetimber.treefall;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;

public class TreeSounds {

    public static void tipOverNoise(Location location) {

        location.getWorld().playSound(location, Sound.BLOCK_CHEST_OPEN, 3F, 0.1F);

    }

    public static void fallNoise(FallingBlock fallingBlock, int counter) {
        if (counter < 20)
            fallingBlock.getWorld().playSound(fallingBlock.getLocation(), Sound.BLOCK_ANVIL_FALL, 3F, 0.1F);
        else
            fallingBlock.getWorld().playSound(fallingBlock.getLocation(), Sound.BLOCK_WOOD_FALL, 3F, 0.1F);

    }

}
