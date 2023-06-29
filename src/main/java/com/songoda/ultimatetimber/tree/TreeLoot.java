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

    /**
     * Gets the tree block type this loot is for
     *
     * @return The tree block type this loot is for
     */
    public TreeBlockType getTreeBlockType() {
        return this.treeBlockType;
    }

    /**
     * Checks if this TreeLoot has an item to drop
     *
     * @return True if an item exists, otherwise false
     */
    public boolean hasItem() {
        return this.item != null;
    }

    /**
     * Gets the item that this tree loot can drop
     *
     * @return An ItemStack this tree loot can drop
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * Checks if this TreeLoot has a command to run
     *
     * @return True if a command exists, otherwise false
     */
    public boolean hasCommand() {
        return this.command != null;
    }

    /**
     * Gets the command that this tree loot can run
     *
     * @return The command that this tree loot can run
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * Gets the percent chance this tree loot will drop
     *
     * @return The percent chance this tree loot can drop
     */
    public double getChance() {
        return this.chance;
    }

    @Override
    public String toString() {
        return "TreeLoot{" +
                "treeBlockType=" + treeBlockType +
                ", item=" + item +
                ", command='" + command + '\'' +
                ", chance=" + chance +
                '}';
    }
}
