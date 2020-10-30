package com.songoda.ultimatetimber.utils;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockType;
import org.bukkit.Location;

public class SoundUtils {

    public static void playFallingSound(ITreeBlock block) {
        Location location = block.getLocation();
        if (location.getWorld() == null) return;
        CompatibleSound.BLOCK_CHEST_OPEN.play(location.getWorld(), location, 2F, 0.1F);
    }

    public static void playLandingSound(ITreeBlock block) {
        Location location = block.getLocation();
        if (location.getWorld() == null) return;

        if (block.getTreeBlockType().equals(TreeBlockType.LOG)) {
            CompatibleSound.BLOCK_WOOD_FALL.play(location.getWorld(), location, 2F, 0.1F);
        } else {
            CompatibleSound.BLOCK_GRASS_BREAK.play(location.getWorld(), location, 0.5F, 0.75F);
        }
    }

}
