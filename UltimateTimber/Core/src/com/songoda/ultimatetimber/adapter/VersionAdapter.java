package com.songoda.ultimatetimber.adapter;

import com.songoda.ultimatetimber.tree.ITreeBlock;
import com.songoda.ultimatetimber.tree.TreeDefinition;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface VersionAdapter {

    /**
     * Gets the version adapter type
     *
     * @return The VersionAdapterType
     */
    VersionAdapterType getVersionAdapterType();

    /**
     * Parses a String into an IBlockData instance
     *
     * @param blockDataString The String to parse
     * @return An IBlockData instance that the given String represents
     */
    IBlockData parseBlockDataFromString(String blockDataString);

    /**
     * Parses a String into an ItemStack
     *
     * @param itemStackString The String to parse
     * @return An ItemStack that the given String represents
     */
    ItemStack parseItemStackFromString(String itemStackString);

    /**
     * Get the items that a tree block should drop when it breaks
     *
     * @param treeDefinition The tree definition to get the items for
     * @param treeBlock The tree block
     * @return A Set of ItemStacks that should be dropped
     */
    Collection<ItemStack> getBlockDrops(TreeDefinition treeDefinition, ITreeBlock treeBlock);

    /**
     * Applies damage to a tool
     *
     * @param player The Player who's tool to apply damage to
     * @param damage The amount of damage to apply
     */
    void applyToolDurability(Player player, int damage);

    /**
     * Checks if a given tool has enough durability remaining
     *
     * @param tool The tool to check
     * @param requiredAmount The amount of durability required
     * @return True if enough durability is remaining to not break the tool, otherwise false
     */
    boolean hasEnoughDurability(ItemStack tool, int requiredAmount);

    /**
     * Gets the item in the player's main hand
     *
     * @param player The Player to get the item from
     * @return The ItemStack in the Player's main hand
     */
    ItemStack getItemInHand(Player player);

    /**
     * Removes the item in the player's main hand
     *
     * @param player The Player to remove the item from
     */
    void removeItemInHand(Player player);

    /**
     * Spawns a falling block at the given location with the given block data
     *
     * @param location The location to spawn at
     * @param block The block to use block data
     * @return A newly spawned FallingBlock entity
     */
    FallingBlock spawnFallingBlock(Location location, Block block);

    /**
     * Configures a falling block for animating
     *
     * @param fallingBlock The falling block to configure
     */
    void configureFallingBlock(FallingBlock fallingBlock);

    /**
     * Enables/Disables gravity for a falling block
     *
     * @param fallingBlock The falling block to apply gravity settings to
     * @param applyGravity Whether or not to apply the gravity
     */
    void toggleGravityFallingBlock(FallingBlock fallingBlock, boolean applyGravity);

    /**
     * Plays particles to indicate a tree block has started falling
     *
     * @param treeDefinition The TreeDefinition of the block
     * @param treeBlock The TreeBlock to play the particles for
     */
    void playFallingParticles(TreeDefinition treeDefinition, ITreeBlock treeBlock);

    /**
     * Plays particles to indicate a tree block has hit the ground
     *
     * @param treeDefinition The TreeDefinition of the block
     * @param treeBlock The TreeBlock to play the particles for
     */
    void playLandingParticles(TreeDefinition treeDefinition, ITreeBlock treeBlock);

    /**
     * Plays a sound to indicate a tree block has started falling
     *
     * @param treeBlock The TreeBlock to play the sound for
     */
    void playFallingSound(ITreeBlock treeBlock);

    /**
     * Plays a sound to indicate a tree block has hit the ground
     *
     * @param treeBlock The TreeBlock to play the sound for
     */
    void playLandingSound(ITreeBlock treeBlock);

}
