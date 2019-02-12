package com.songoda.ultimatetimber.hooks;

import java.lang.reflect.Method;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.songoda.ultimatetimber.utils.WoodToLogConverter;

public class McMMOHook {

    private static boolean enabled = false;
    private static Enum<?> woodcuttingEnum;
    private static Method getXpMethod;
    
    /**
     * Updates a Player's Wood Cutting skill based on the number of logs they broke
     * 
     * @param player The Player to update
     * @param treeBlocks The tree blocks that were broken
     */
    public static void updateWoodCuttingSkill(Player player, HashSet<Block> treeBlocks) {
        if (!enabled) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        
        try {
            ExperienceAPI.addXP(player, "woodcutting", getLogXP(treeBlocks), "pve");
        } catch (Exception ex) {
            Bukkit.getLogger().warning("[UltimateTimber] Warning: The version of mcMMO you are using is not compatible with UltimateTimber. Disabling hook...");
            enabled = false;
        }
    }
    
    /**
     * Sets the hook to enabled
     */
    public static boolean setEnabled() {
        try { // Try to find mcMMO Overhaul
            Class<?> primarySkillTypeClass = Class.forName("com.gmail.nossr50.datatypes.skills.PrimarySkillType");
            for (Object enumValue : primarySkillTypeClass.getEnumConstants()) {
                Enum<?> primarySkillTypeEnum = (Enum<?>) enumValue;
                if (primarySkillTypeEnum.name().equals("WOODCUTTING")) {
                    woodcuttingEnum = primarySkillTypeEnum;
                    break;
                }
            }
            getXpMethod = ExperienceConfig.class.getMethod("getXp", woodcuttingEnum.getClass(), Material.class);
        } catch (Exception ex) {
            try { // Try to find mcMMO Classic
                Class<?> skillTypeClass = Class.forName("com.gmail.nossr50.datatypes.skills.SkillType");
                for (Object enumValue : skillTypeClass.getEnumConstants()) {
                    Enum<?> skillTypeEnum = (Enum<?>) enumValue;
                    if (skillTypeEnum.name().equals("WOODCUTTING")) {
                        woodcuttingEnum = skillTypeEnum;
                        break;
                    }
                }
                getXpMethod = ExperienceConfig.class.getMethod("getXp", woodcuttingEnum.getClass(), Material.class);
            } catch (Exception ex2) {
                return false;
            }
        }
        
        enabled = true;
        return true;
    }
    
    /**
     * Gets the XP that a player would obtain from breaking the logs in the tree
     * 
     * @param treeBlocks The potential log blocks
     * @return The XP a player should gain
     */
    private static int getLogXP(HashSet<Block> treeBlocks) throws Exception {
        int xp = 0;
        for (Block block : treeBlocks) {
            Material material = WoodToLogConverter.convert(block.getType());
            if (!material.name().endsWith("LOG")) continue;
            xp += (int) getXpMethod.invoke(ExperienceConfig.getInstance(), woodcuttingEnum, material);
        }
        return xp;
    }

}
