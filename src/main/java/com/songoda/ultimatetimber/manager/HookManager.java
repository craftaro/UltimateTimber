package com.songoda.ultimatetimber.manager;

import java.util.HashSet;
import java.util.Set;

import com.songoda.ultimatetimber.hooks.JobsRebornHook;
import com.songoda.ultimatetimber.hooks.McMMOHook;
import com.songoda.ultimatetimber.hooks.TimberHook;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class HookManager {

    private static HookManager instance;
    
    private Set<TimberHook> hooks;
    
    private HookManager() {
        this.hooks = new HashSet<>();
    }
    
    /**
     * Gets the instance of the HookManager
     * 
     * @return The instance of the HookManager
     */
    public static HookManager getInstance() {
        if (instance == null)
            instance = new HookManager();
        return instance;
    }
    
    /**
     * Hooks into compatible plugins
     */
    public void hook() {
        this.tryHook("mcMMO", McMMOHook.class);
        this.tryHook("Jobs", JobsRebornHook.class);
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
     * Applies the loaded hooks
     * 
     * @param player The player to apply the hook for
     * @param treeBlocks The blocks of the tree that were broken
     */
    public void applyHooks(Player player, HashSet<Block> treeBlocks) {
        Set<TimberHook> invalidHooks = new HashSet<>();
        for (TimberHook hook : this.hooks) {
            try {
                hook.apply(player, treeBlocks);
            } catch (Exception ex) {
                invalidHooks.add(hook);
            }
        }
        
        for (TimberHook hook : invalidHooks)
            this.hooks.remove(hook);
    }

}
