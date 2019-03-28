package com.songoda.ultimatetimber.old_code;

import org.bukkit.block.Block;

import java.util.HashSet;

class NoAnimationTreeDestroyer {

    /*
    Only ever triggers when people have tree falling animations off in the config
     */
    static void destroyTree(HashSet<Block> blocks, boolean hasBonusLoot, boolean hasSilkTouch) {
        // Drop loot and plant a new sapling
        for (Block block : blocks) {
            TreeLoot.dropTreeLoot(block.getBlockData(), block.getLocation().clone().add(0.5, 0.5, 0.5), hasBonusLoot, hasSilkTouch);
            TreeReplant.replaceOriginalBlock(block);
        }
    }

}
