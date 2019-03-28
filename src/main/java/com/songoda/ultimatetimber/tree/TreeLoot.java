package com.songoda.ultimatetimber.tree;

import org.bukkit.inventory.ItemStack;

public class TreeLoot {

    private final TreeBlockType treeBlockType;
    private final ItemStack item;
    private final String command;
    private final double chance;

    public TreeLoot(TreeBlockType treeBlockType, ItemStack item, String command, double chance) {
        this.treeBlockType = treeBlockType;
        this.item = item;
        this.command = command;
        this.chance = chance;
    }

    public void tryDropLoot(ITreeBlock treeBlock) {

    }

}
