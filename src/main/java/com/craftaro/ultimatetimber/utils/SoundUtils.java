package com.craftaro.ultimatetimber.utils;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.ultimatetimber.tree.ITreeBlock;
import com.craftaro.ultimatetimber.tree.TreeBlockType;
import org.bukkit.Location;

public class SoundUtils {
    public static void playFallingSound(ITreeBlock block) {
        Location location = block.getLocation();
        if (location.getWorld() == null) {
            return;
        }
        XSound.BLOCK_CHEST_OPEN.play(location, 2, .1f);
    }

    public static void playLandingSound(ITreeBlock block) {
        Location location = block.getLocation();
        if (location.getWorld() == null) {
            return;
        }

        if (block.getTreeBlockType() == TreeBlockType.LOG) {
            XSound.BLOCK_WOOD_FALL.play(location, 2, .1f);
        } else {
            XSound.BLOCK_GRASS_BREAK.play(location, .5f, .75f);
        }
    }
}
