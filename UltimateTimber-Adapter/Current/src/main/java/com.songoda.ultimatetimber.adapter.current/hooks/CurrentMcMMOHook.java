package com.songoda.ultimatetimber.adapter.current.hooks;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.songoda.ultimatetimber.hook.TimberHook;
import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class CurrentMcMMOHook implements TimberHook {

    private Enum<?> woodcuttingEnum;
    private Method getXpMethod;

    public CurrentMcMMOHook() throws NoSuchMethodException, SecurityException, ClassNotFoundException {
        try { // Try to find mcMMO Overhaul
            Class<?> primarySkillTypeClass = Class.forName("com.gmail.nossr50.datatypes.skills.PrimarySkillType");
            for (Object enumValue : primarySkillTypeClass.getEnumConstants()) {
                Enum<?> primarySkillTypeEnum = (Enum<?>) enumValue;
                if (primarySkillTypeEnum.name().equals("WOODCUTTING")) {
                    this.woodcuttingEnum = primarySkillTypeEnum;
                    break;
                }
            }
            this.getXpMethod = ExperienceConfig.class.getMethod("getXp", this.woodcuttingEnum.getClass(), Material.class);
        } catch (Exception ex) {
            Class<?> skillTypeClass = Class.forName("com.gmail.nossr50.datatypes.skills.SkillType");
            for (Object enumValue : skillTypeClass.getEnumConstants()) {
                Enum<?> skillTypeEnum = (Enum<?>) enumValue;
                if (skillTypeEnum.name().equals("WOODCUTTING")) {
                    this.woodcuttingEnum = skillTypeEnum;
                    break;
                }
            }
            this.getXpMethod = ExperienceConfig.class.getMethod("getXp", this.woodcuttingEnum.getClass(), Material.class);
        }
    }

    @Override
    public void apply(Player player, TreeBlockSet<Block> treeBlocks) throws Exception {
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;

        int xp = 0;
        for (ITreeBlock<Block> treeBlock : treeBlocks.getLogBlocks()) {
            Block block = treeBlock.getBlock();
            Material material = block.getType();
            xp += (int) this.getXpMethod.invoke(ExperienceConfig.getInstance(), this.woodcuttingEnum, material);
        }

        ExperienceAPI.addXP(player, "woodcutting", xp, "pve");
    }

}
