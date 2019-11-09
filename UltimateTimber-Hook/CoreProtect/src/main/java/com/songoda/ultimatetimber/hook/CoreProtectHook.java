package com.songoda.ultimatetimber.hook;

import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.utils.NMSUtil;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CoreProtectHook implements TimberHook {

    private CoreProtectAPI api;
    private boolean useDeprecatedMethod = NMSUtil.getVersionNumber() <= 12;

    public CoreProtectHook() {
        this.api = CoreProtect.getInstance().getAPI();
    }

    @Override
    public void applyExperience(Player player, TreeBlockSet<Block> treeBlocks) {
        if (!this.api.isEnabled())
            return;

        for (ITreeBlock<Block> treeBlock : treeBlocks.getAllTreeBlocks()) {
            if (this.useDeprecatedMethod) {
                this.api.logRemoval(player.getName(), treeBlock.getLocation(), treeBlock.getBlock().getType(), treeBlock.getBlock().getData());
            } else {
                this.api.logRemoval(player.getName(), treeBlock.getLocation(), treeBlock.getBlock().getType(), treeBlock.getBlock().getBlockData());
            }
        }
    }

    @Override
    public boolean shouldApplyDoubleDrops(Player player) {
        return false;
    }

    @Override
    public boolean isUsingAbility(Player player) {
        return false;
    }

}
