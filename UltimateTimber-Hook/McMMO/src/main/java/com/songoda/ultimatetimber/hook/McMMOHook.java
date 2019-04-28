package com.songoda.ultimatetimber.hook;

import com.gmail.nossr50.api.AbilityAPI;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class McMMOHook implements TimberHook {

    @Override
    public void applyExperience(Player player, TreeBlockSet<Block> treeBlocks) {
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;

        int xp = 0;
        for (ITreeBlock<Block> treeBlock : treeBlocks.getLogBlocks()) {
            Block block = treeBlock.getBlock();
            BlockData blockData = block.getBlockData();
            xp += ExperienceConfig.getInstance().getXp(PrimarySkillType.WOODCUTTING, blockData);
        }

        ExperienceAPI.addXP(player, "woodcutting", xp, "pve");
    }

    @Override
    public boolean shouldApplyDoubleDrops(Player player) {
        if (PrimarySkillType.WOODCUTTING.getDoubleDropsDisabled())
            return false;

        return Permissions.isSubSkillEnabled(player, SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RankUtils.hasReachedRank(1, player, SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.WOODCUTTING_HARVEST_LUMBER, player);
    }

    @Override
    public boolean isUsingAbility(Player player) {
        return AbilityAPI.treeFellerEnabled(player);
    }

}
