package com.songoda.ultimatetimber.manager;

import com.songoda.ultimatetimber.UltimateTimber;
import com.songoda.ultimatetimber.adapter.VersionAdapterType;
import com.songoda.ultimatetimber.hook.CoreProtectHook;
import com.songoda.ultimatetimber.hook.JobsHook;
import com.songoda.ultimatetimber.hook.McMMOClassic12Hook;
import com.songoda.ultimatetimber.hook.McMMOClassic13Hook;
import com.songoda.ultimatetimber.hook.McMMOClassic8Hook;
import com.songoda.ultimatetimber.hook.McMMOHook;
import com.songoda.ultimatetimber.hook.TimberHook;
import com.songoda.ultimatetimber.tree.TreeBlockSet;
import com.songoda.ultimatetimber.utils.NMSUtil;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HookManager extends Manager {

    private Set<TimberHook> hooks;

    public HookManager(UltimateTimber ultimateTimber) {
        super(ultimateTimber);
        this.hooks = new HashSet<>();
    }

    @Override
    public void reload() {
        this.hooks.clear();

        this.tryHook("Jobs", JobsHook.class);
        this.tryHook("CoreProtect", CoreProtectHook.class);

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (this.plugin.getVersionAdapter().getVersionAdapterType().equals(VersionAdapterType.CURRENT)) {
                Plugin mcMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
                if (mcMMO != null) {
                    String version = mcMMO.getDescription().getVersion();
                    if (version.startsWith("2")) {
                        this.tryHook("mcMMO", McMMOHook.class);
                    } else {
                        this.tryHook("mcMMO", McMMOClassic13Hook.class);
                    }
                }
            } else {
                if (NMSUtil.getVersionNumber() == 12) {
                    this.tryHook("mcMMO", McMMOClassic12Hook.class);
                } else if (NMSUtil.getVersionNumber() == 8) {
                    this.tryHook("mcMMO", McMMOClassic8Hook.class);
                }
            }
        });
    }

    @Override
    public void disable() {
        this.hooks.clear();
    }
    
    /**
     * Tries to hook into a compatible plugin
     * 
     * @param pluginName The name of the plugin
     * @param hookClass The hook class
     */
    private void tryHook(String pluginName, Class<? extends TimberHook> hookClass) {
        if (!Bukkit.getPluginManager().isPluginEnabled(pluginName)) 
            return;
        
        try {
            this.hooks.add(hookClass.newInstance());
            Bukkit.getConsoleSender().sendMessage(String.format("Hooks: Hooked into %s!", pluginName));
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(String.format("Hooks: Unable to hook with %s, the version installed is not supported!", pluginName));
        }
    }
    
    /**
     * Applies experience to the loaded hooks
     *
     * @param player The player to apply experience to
     * @param treeBlocks The blocks of the tree that were broken
     */
    public void applyExperienceHooks(Player player, TreeBlockSet<Block> treeBlocks) {
        if (!ConfigurationManager.Setting.HOOKS_APPLY_EXPERIENCE.getBoolean())
            return;

        for (TimberHook hook : this.hooks)
            hook.applyExperience(player, treeBlocks);
    }

    /**
     * Checks if double drops should be applied from the loaded hooks
     *
     * @param player The player to check
     */
    public boolean shouldApplyDoubleDropsHooks(Player player) {
        if (!ConfigurationManager.Setting.HOOKS_APPLY_EXTRA_DROPS.getBoolean())
            return false;

        for (TimberHook hook : this.hooks)
            if (hook.shouldApplyDoubleDrops(player))
                return true;
        return false;
    }

    /**
     * Checks if a player is using an ability from the loaded hooks
     *
     * @param player The player to check
     */
    public boolean isUsingAbilityHooks(Player player) {
        if (!ConfigurationManager.Setting.HOOKS_REQUIRE_ABILITY_ACTIVE.getBoolean() || this.hooks.isEmpty())
            return true;

        for (TimberHook hook : this.hooks)
            if (hook.isUsingAbility(player))
                return true;
        return false;
    }

}
